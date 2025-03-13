package com.marks2games.gravitygame.building_game.data.repository

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.PlanetResource
import com.marks2games.gravitygame.building_game.domain.repository.PlanetRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlanetRepositoryImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val user: FirebaseUser?
) : PlanetRepository{
    override suspend fun getPlanet(planetId: Int): Planet {
        if(user == null) return Planet()
        val planetDoc = database
            .collection("empires")
            .document(user.uid)
            .collection("planets")
            .document(planetId.toString())
            .get()
            .await()

        return planetDoc.data?.let { Planet.fromMap(it) }
            ?: throw IllegalStateException ("Planet not found")
    }

    override suspend fun getPlanetResource(planetId: Int, resource: PlanetResource): Double {
        if(user == null) return 0.0
        val planetDoc = database
            .collection("empires")
            .document(user.uid)
            .collection("planets")
            .document(planetId.toString())
            .get(Source.SERVER)
            .await()

        return planetDoc.getDouble(PlanetResource.BIOMASS.name) ?: 0.0
    }



    override suspend fun updatePlanet(planet: Planet) {
        if(user == null) return
        database
            .collection("empires")
            .document(user.uid)
            .collection("planets")
            .document(planet.id.toString())
            .set(planet)
            .await()
    }

    override suspend fun updatePlanetResource(
        planetId: Int,
        resource: PlanetResource,
        value: Double
    ) {
        if(user == null) return
        database
            .collection("empires")
            .document(user.uid)
            .collection("planets")
            .document(planetId.toString())
            .update(resource.name, value)
            .await()

    }

    override suspend fun updatePlanetLevel(planetId: Int, level: Int) {
        if(user == null) return
        database
            .collection("empires")
            .document(user.uid)
            .collection("planets")
            .document(planetId.toString())
            .update("level", level)
            .await()

    }


}