package com.marks2games.gravitygame.building_game.domain.usecase.useractions

import com.marks2games.gravitygame.building_game.data.model.EmpireResource
import com.marks2games.gravitygame.building_game.data.model.Planet
import javax.inject.Inject

class SetProductionGeneralResourcesUseCase @Inject constructor(){
    operator fun invoke(resource: EmpireResource, value: Int, planet: Planet): Planet {



        return planet
    }
}