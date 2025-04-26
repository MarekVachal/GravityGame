package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import android.util.Log
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.Transport
import javax.inject.Inject

class TransportInUseCase @Inject constructor() {
    operator fun invoke(transports: List<Transport>, planet: Planet): Planet {
        Log.d("Transport", "Transporting in: $transports")
        var updatedPlanet = planet
        transports.forEach{ transport ->
            val import = if(transport.planet1Id == planet.id) transport.exportFromPlanet2 else transport.exportFromPlanet1
            updatedPlanet = updatedPlanet.copy(
                metal = updatedPlanet.metal + (import[Resource.METAL] ?: 0),
                organicSediment = updatedPlanet.organicSediment + (import[Resource.ORGANIC_SEDIMENTS]?.toFloat()
                    ?: 0f),
                rocketMaterials = updatedPlanet.rocketMaterials + (import[Resource.ROCKET_MATERIALS]
                    ?: 0)
            )
            Log.d("Transport", "Updated planet: $updatedPlanet")
        }
        return updatedPlanet
    }
}