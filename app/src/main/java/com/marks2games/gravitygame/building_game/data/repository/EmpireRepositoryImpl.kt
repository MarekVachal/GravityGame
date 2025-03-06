package com.marks2games.gravitygame.building_game.data.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.marks2games.gravitygame.building_game.data.model.Empire
import com.marks2games.gravitygame.building_game.data.model.EmpireResource
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.domain.repository.EmpireRepository
import io.sentry.Sentry
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmpireRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val user: FirebaseUser
) : EmpireRepository {
    override suspend fun getEmpire(): Empire {
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

    override suspend fun updateEmpire(empire: Empire) {
        try {
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
        } catch (e: Exception) {
            Sentry.captureException(e)
        }
    }

    override suspend fun getEmpireResource(resource: EmpireResource): Double {
        return try {
            val snapshot = firestore
                .collection("empires")
                .document(user.uid)
                .get(Source.SERVER)
                .await()
            snapshot.getDouble(resource.name) ?: 0.0
        } catch (e: Exception) {
            Sentry.captureException(e)
            0.0
        }
    }

    override suspend fun updateEmpireResource(resource: EmpireResource, value: Double) {
        try{
            firestore
                .collection("empires")
                .document(user.uid)
                .update(resource.name, value)
                .await()
        } catch (e: Exception){
            Sentry.captureException(e)
        }
    }

    override suspend fun saveTurn(value: Int) {
        try{
            firestore
                .collection("empires")
                .document(user.uid)
                .update("savedTurns", value)
                .await()
        } catch (e: Exception){
            Sentry.captureException(e)
        }
    }

    override suspend fun updateUpdateTime(value: Long) {
        try{
            firestore
                .collection("empires")
                .document(user.uid)
                .update("lastUpdated", value)
                .await()
        } catch (e: Exception){
            Sentry.captureException(e)
        }
    }

}