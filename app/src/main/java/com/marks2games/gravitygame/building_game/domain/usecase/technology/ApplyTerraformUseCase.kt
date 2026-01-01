package com.marks2games.gravitygame.building_game.domain.usecase.technology

import com.marks2games.gravitygame.building_game.data.model.Technology
import com.marks2games.gravitygame.building_game.data.model.TechnologyEnum
import javax.inject.Inject
import kotlin.math.ceil

class ApplyTerraformUseCase @Inject constructor() {
    operator fun invoke(basePrice: Int, technologies: List<Technology>): Int {
        val terraform = (technologies.find {it.type == TechnologyEnum.TERRAFORM } as? Technology.MultiplyTechnology.Terraform)
        val terraformLvl = terraform?.level?: 0
        val coefficient = terraform?.coefficient ?: 1f
        var price = basePrice.toFloat()
        repeat(terraformLvl){
            price *= coefficient
        }
        return ceil(price).toInt()
    }
}