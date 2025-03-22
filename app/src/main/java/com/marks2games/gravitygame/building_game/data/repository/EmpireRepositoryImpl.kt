package com.marks2games.gravitygame.building_game.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.domain.repository.EmpireRepository
import io.sentry.Sentry
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmpireRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val user: FirebaseUser?
) : EmpireRepository {
    override suspend fun getEmpire(): Empire {
        if(user == null) return Empire()
        try {
            val snapshot = firestore
                .collection("empires")
                .document(user.uid)
                .get()
                .await()
            if(!snapshot.exists()) {
                val newEmpire = Empire()
                updateEmpire(newEmpire)
                return newEmpire
            }

            val empire = Empire.fromMap(snapshot.data ?: emptyMap())
            val planetsCollection = firestore
                .collection("empires")
                .document(user.uid)
                .collection("planets")
            val planets = planetsCollection
                .get()
                .await()
                .map { Planet.fromMap(it.data) }
            return empire.copy(planets = planets)
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
        return Empire()
    }

    override suspend fun getPlanet(planetId: Int): Planet {
        if(user == null) return Planet()
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
            empire.planets.forEach { planet ->
                planetsCollection
                    .document(planet.id.toString())
                    .set(planet.toMap())
                    .await()
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