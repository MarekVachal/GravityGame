package com.marks2games.gravitygame.building_game.domain.usecase.utils

import com.marks2games.gravitygame.building_game.data.model.Resource
import javax.inject.Inject

class GetConsumedResourceUseCase @Inject constructor() {
    operator fun invoke(producedResource: Resource, isForProspectors: Boolean): Pair<Resource?, Resource?>{
        val (consumedResource1, consumedResource2) = when(producedResource){
            Resource.RESEARCH -> Resource.BIOMASS to null
            Resource.TRADE_POWER -> Resource.INFRASTRUCTURE to null
            Resource.ARMY -> Resource.ROCKET_MATERIALS to null
            Resource.CREDITS -> null to null
            Resource.EXPEDITIONS -> Resource.ROCKET_MATERIALS to null
            Resource.BIOMASS -> null to null
            Resource.METAL -> {
                if(!isForProspectors){
                    Resource.ORGANIC_SEDIMENTS to null
                } else {
                    null to null
                }
            }
            Resource.ORGANIC_SEDIMENTS -> null to null
            Resource.INFRASTRUCTURE -> Resource.METAL to null
            Resource.ROCKET_MATERIALS -> Resource.ORGANIC_SEDIMENTS to Resource.METAL
            Resource.PROGRESS -> Resource.INFRASTRUCTURE to Resource.BIOMASS
            Resource.DEVELOPMENT -> Resource.INFRASTRUCTURE to null
            Resource.INFLUENCE -> null to null
        }
        return consumedResource1 to consumedResource2
    }
}