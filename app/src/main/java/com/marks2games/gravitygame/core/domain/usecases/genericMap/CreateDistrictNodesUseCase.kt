package com.marks2games.gravitygame.core.domain.usecases.genericMap

import androidx.compose.ui.graphics.Color
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.PlanetType
import com.marks2games.gravitygame.core.data.model.DistrictNode
import javax.inject.Inject

class CreateDistrictNodesUseCase @Inject constructor() {
    operator fun invoke(planetType: PlanetType, districts: List<District>): List<DistrictNode>{
        return districts.map{ district ->
            val districtNode = planetType.planetMapConfig.find { it.id.toInt() == district.districtId }?: return emptyList()

            DistrictNode(
                district = district,
                id = district.districtId.toString(),
                posX = districtNode.posX,
                posY = districtNode.posY,
                connections = districtNode.connections,
                buttonColor = Color.White
            )
        }
    }
}