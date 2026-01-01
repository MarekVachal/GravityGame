package com.marks2games.gravitygame.building_game.domain.usecase.utils

import com.marks2games.gravitygame.R
import com.marks2games.gravitygame.building_game.data.model.Resource
import javax.inject.Inject

class GetResourceIconUseCase @Inject constructor() {
    operator fun invoke(resource: Resource): Int{
        return when(resource){
            Resource.ARMY -> R.drawable.army_icon
            Resource.BIOMASS -> R.drawable.biomass_icon
            Resource.METAL -> R.drawable.metal_icon
            Resource.ORGANIC_SEDIMENTS -> R.drawable.organic_sediments_icon
            Resource.INFRASTRUCTURE -> R.drawable.infrastructure_icon
            Resource.ROCKET_MATERIALS -> R.drawable.rocket_material_icon
            Resource.PROGRESS -> R.drawable.progress_icon
            Resource.DEVELOPMENT -> R.drawable.development_icon
            Resource.INFLUENCE -> R.drawable.influence_icon
            else -> R.drawable.cruiser
        }
    }
}