package com.marks2games.gravitygame.building_game.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseUser
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
    private val user: FirebaseUser?,
    private val createNewEmpireUseCase: CreateNewEmpireUseCase
) : EmpireRepository {
    override suspend fun getEmpire(): Empire {
        if(user == null) return createNewEmpireUseCase.invoke()
        try {
            val snapshot = firestore
                .collection("empires")
                .document(user.uid)
                .get()
                .await()
            if(snapshot.exists()){
                val empire = Empire.fromMap(snapshot.data ?: emptyMap())
                val planetsCollection = firestore
                    .collection("empires")
                    .document(user.uid)
                    .collection("planets")
                val planets = planetsCollection
                    .get()
                    .await()
                    .map { Planet.fromMap(it.data) }
                return empire.copy(
                    planets = planets,
                    planetsCount = planets.size
                )
            } else {
                val newEmpire = createNewEmpireUseCase.invoke()
                updateEmpire(newEmpire)
                return newEmpire
            }
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
        return createNewEmpireUseCase.invoke()
    }

    override suspend fun getPlanet(planetId: Int): Planet {
        if(user == null) return Planet(type = SmallPlanet)
        val planetDoc = firestore
            .collection("empires")
            .document(user.uid)
            .collection("planets")
            .document(planetId.toString())
            .get()
            .await()

        return planetDoc.data?.let { Planet.fromMap(it) }
            ?: throw IllegalStateException ("Planet not found")
    }

    override suspend fun updateEmpire(empire: Empire) {
        if (user == null) return
        try {
            Log.d("Firebase", "Updating empire: ${empire.toMap()}")
            firestore
                .collection("empires")
                .document(user.uid)
                .set(empire.toMap())
                .await()
            val planetsCollection = firestore
                .collection("empires")
                .document(user.uid)
                .collection("planets")
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
        if(user == null) return
        firestore
            .collection("empires")
            .document(user.uid)
            .collection("planets")
            .document(planet.id.toString())
            .set(planet)
            .await()
    }
}