package com.marks2games.gravitygame.building_game.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.SmallPlanet
import com.marks2games.gravitygame.building_game.domain.repository.EmpireRepository
import com.marks2games.gravitygame.building_game.domain.usecase.utils.CreateNewEmpireUseCase
import io.sentry.Sentry
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmpireRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val createNewEmpireUseCase: CreateNewEmpireUseCase
) : EmpireRepository {
    private fun currentEmpireDocument(): DocumentReference? {
        val uid = auth.currentUser?.uid ?: return null
        return firestore.collection("empires").document(uid)
    }

    override suspend fun getEmpire(): Empire {
        val empireDoc = currentEmpireDocument() ?: return createNewEmpireUseCase.invoke()
        try {
            val snapshot = empireDoc.get().await()
            if(snapshot.exists()){
                val empire = Empire.fromMap(snapshot.data ?: emptyMap())
                val planets = empireDoc.collection("planets")
                    .get()
                    .await()
                    .map { Planet.fromMap(it.data) }
                return empire.copy(
                    planets = planets,
                    planetsCount = planets.size
                )
            } else {
                val newEmpire = createNewEmpireUseCase.invoke()
                updateEmpire(empireDoc, newEmpire)
                return newEmpire
            }
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
        return createNewEmpireUseCase.invoke()
    }

    override suspend fun getPlanet(planetId: Int): Planet {
        val empireDoc = currentEmpireDocument() ?: return Planet(type = SmallPlanet)
        val planetDoc = empireDoc
            .collection("planets")
            .document(planetId.toString())
            .get()
            .await()

        return planetDoc.data?.let { Planet.fromMap(it) }
            ?: throw IllegalStateException ("Planet not found")
    }

    override suspend fun updateEmpire(empire: Empire) {
        val empireDoc = currentEmpireDocument() ?: return
        updateEmpire(empireDoc, empire)
    }

    private suspend fun updateEmpire(empireDoc: DocumentReference, empire: Empire) {
        try {
            Log.d("Firebase", "Updating empire: ${empire.toMap()}")
            empireDoc.set(empire.toMap()).await()
            val planetsCollection = empireDoc.collection("planets")
            coroutineScope {
                empire.planets.map { planet ->
                    async {
                        planetsCollection
                            .document(planet.id.toString())
                            .set(planet.toMap())
                            .await()
                    }
                }.awaitAll()
            }
            Log.d("Firebase", "Empire updated successfully.")
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }

    override suspend fun updatePlanet(planet: Planet) {
        val empireDoc = currentEmpireDocument() ?: return
        empireDoc
            .collection("planets")
            .document(planet.id.toString())
            .set(planet)
            .await()
    }

    override suspend fun deleteEmpire() {
        val empireDoc = currentEmpireDocument() ?: return
        try {
            val planetsCollection = empireDoc.collection("planets")
            val planetDocs = planetsCollection.get().await()
            coroutineScope {
                planetDocs.map { doc ->
                    async {
                        planetsCollection.document(doc.id).delete().await()
                    }
                }.awaitAll()
            }

            empireDoc.delete().await()
            Log.d("Firebase", "Empire and all its planets deleted successfully.")
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }

    override suspend fun deletePlanet(planetId: Int) {
        val empireDoc = currentEmpireDocument() ?: return
        try {
            empireDoc
                .collection("planets")
                .document(planetId.toString())
                .delete()
                .await()
            Log.d("Firebase", "Planet $planetId deleted successfully.")
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }
}