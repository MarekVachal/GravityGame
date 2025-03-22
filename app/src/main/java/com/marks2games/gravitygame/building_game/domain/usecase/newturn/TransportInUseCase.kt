package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.Transport
import javax.inject.Inject

class TransportInUseCase @Inject constructor() {
    operator fun invoke(transports: List<Transport>, planet: Planet): Planet {
        return transports.fold(planet) { updatedPlanet, transport ->
            updatedPlanet.copy(
                metal = updatedPlanet.metal + (transport.exportFromPlanet2[Resource.METAL] ?: 0),
                organicSediment = updatedPlanet.organicSediment + (transport.exportFromPlanet2[Resource.ORGANIC_SEDIMENTS]?.toFloat()
                    ?: 0f),
                rocketMaterials = updatedPlanet.rocketMaterials + (transport.exportFromPlanet2[Resource.ROCKET_MATERIALS]
                    ?: 0)
            )
        }
    }
}