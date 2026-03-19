package com.marks2games.gravitygame.building_game.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.marks2games.gravitygame.R

enum class Resource (
    @get:StringRes val nameResIdNominative: Int,
    @get:StringRes val nameResIdGenitive: Int,
    @get:StringRes val descriptionId: Int,
    @get:DrawableRes val icon: Int
){
    RESEARCH (R.string.research, R.string.researchGenitive, R.string.researchDescription, R.drawable.research_icon),
    TRADE_POWER (R.string.tradepower, R.string.tradepowerGenitive, R.string.tradepowerDescription, R.drawable.progress_icon),
    ARMY (R.string.army, R.string.armyGenitive, R.string.armyDescription, R.drawable.warship_material_icon),
    CREDITS (R.string.credits, R.string.creditsGenitive, R.string.creditsDescription, R.drawable.progress_icon),
    EXPEDITIONS (R.string.expeditions, R.string.expeditionsGenitive, R.string.expeditionsDescription, R.drawable.cruiser),
    BIOMASS (R.string.biomass, R.string.biomassGenitive, R.string.biomassDescription, R.drawable.biomass_icon),
    METAL (R.string.metal, R.string.metalGenitive, R.string.metalDescription, R.drawable.metal_icon),
    ORGANIC_SEDIMENTS (R.string.organic_sediments, R.string.organic_sedimentsGenitive, R.string.organic_sedimentsDescritpion, R.drawable.organic_sediments_icon),
    INFRASTRUCTURE (R.string.infrastructure, R.string.infrastructureGenitive, R.string.infrastructureDescription, R.drawable.infrastructure_icon),
    ROCKET_MATERIALS (R.string.rocket_materials, R.string.rocketMaterialsGenitive, R.string.rocket_materialsDescription, R.drawable.rocket_material_icon),
    PROGRESS (R.string.progress, R.string.progressGenitive, R.string.progressDescription, R.drawable.progress_icon),
    DEVELOPMENT (R.string.development, R.string.developmentGenitive, R.string.developmentDescription, R.drawable.development_icon),
    INFLUENCE (R.string.influence, R.string.influenceGenitive, R.string.influenceDescription, R.drawable.influence_icon)
}
