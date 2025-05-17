package com.marks2games.gravitygame.building_game.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.marks2games.gravitygame.R

enum class Resource (
    @StringRes val nameResIdNominative: Int,
    @StringRes val nameResIdGenitive: Int,
    @DrawableRes val icon: Int
){
    RESEARCH (R.string.research, R.string.researchGenitive, R.drawable.research_icon),
    TRADE_POWER (R.string.tradepower, R.string.tradepowerGenitive, R.drawable.progress_icon),
    ARMY (R.string.army, R.string.armyGenitive, R.drawable.army_icon),
    CREDITS (R.string.credits, R.string.creditsGenitive, R.drawable.progress_icon),
    EXPEDITIONS (R.string.expeditions, R.string.expeditionsGenitive, R.drawable.cruiser),
    BIOMASS (R.string.biomass, R.string.biomassGenitive, R.drawable.biomass_icon),
    METAL (R.string.metal, R.string.metalGenitive, R.drawable.metal_icon),
    ORGANIC_SEDIMENTS (R.string.organic_sediments, R.string.organic_sedimentsGenitive, R.drawable.organic_sediments_icon),
    INFRASTRUCTURE (R.string.infrastructure, R.string.infrastructureGenitive, R.drawable.infrastructure_icon),
    ROCKET_MATERIALS (R.string.rocket_materials, R.string.rocketMaterialsGenitive, R.drawable.rocket_material_icon),
    PROGRESS (R.string.progress, R.string.progressGenitive, R.drawable.progress_icon),
    DEVELOPMENT (R.string.development, R.string.developmentGenitive, R.drawable.development_icon),
    INFLUENCE (R.string.influence, R.string.influenceGenitive, R.drawable.influence_icon)
}