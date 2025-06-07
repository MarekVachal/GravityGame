package com.marks2games.gravitygame.building_game.data.model

import androidx.annotation.StringRes
import com.marks2games.gravitygame.R
import io.sentry.Sentry

sealed interface Technology{
    val nameId: Int
    val descriptionId: Int
    val type: TechnologyEnum
    val cost: Int
    val state: TechnologyResearchState
    val dependencies: List<TechnologyEnum>
    val posX: Float
    val posY: Float

    fun toMap(): Map<String, Any> = when (this) {
        is SimpleTechnology -> mapOf("state" to state.name)
        is SynergyTechnology -> mapOf("state" to state.name)
        is MultiplyTechnology -> mapOf(
            "level" to level,
            "state" to state.name
        )
    }

    fun changeTechnologyState(newState : TechnologyResearchState): Technology

    sealed class SimpleTechnology : Technology{

        data class RocketScience(
            override val state: TechnologyResearchState = TechnologyResearchState.UNLOCKED,
            override val nameId: Int = R.string.rocketScienceNominative,
            override val descriptionId: Int = R.string.rocketScienceDescription,
            override val type: TechnologyEnum = TechnologyEnum.ROCKET_SCIENCE,
            override val cost: Int = 50,
            override val dependencies: List<TechnologyEnum> = emptyList(),
            override val posX: Float = 0.1f,
            override val posY: Float = 0.7f
        ): SimpleTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return this.copy(state = newState)
            }
        }

        data class SyntheticMaterials(
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.syntheticMaterialsNominative,
            override val descriptionId: Int = R.string.syntheticMaterialsDescription,
            override val type: TechnologyEnum = TechnologyEnum.SYNTHETIC_MATERIALS,
            override val cost: Int = 1000,
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.ROCKET_SCIENCE),
            override val posX: Float = 0.25f,
            override val posY: Float = 0.7f
        ) : SimpleTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return this.copy(state = newState)
            }
        }

        data class SpaceTravelling(
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.spaceTravellingNominative,
            override val descriptionId: Int = R.string.spaceTravellingDescription,
            override val type: TechnologyEnum = TechnologyEnum.SPACE_TRAVELLING,
            override val cost: Int = 100,
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.ROCKET_SCIENCE),
            override val posX: Float = 0.25f,
            override val posY: Float = 0.4f
        ): SimpleTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return this.copy(state = newState)
            }
        }

        data class TransportTechnology(
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.transportTechnologyNominative,
            override val descriptionId: Int = R.string.transportTechnologyDescription,
            override val type: TechnologyEnum = TechnologyEnum.TRANSPORT_TECHNOLOGY,
            override val cost: Int = 200,
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.SPACE_TRAVELLING),
            override val posX: Float = 0.4f,
            override val posY: Float = 0.7f
        ) : SimpleTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return this.copy(state = newState)
            }
        }

        data class InfluenceTechnology(
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.influenceTechnologyNominative,
            override val descriptionId: Int = R.string.influenceTechnologyDescription,
            override val type: TechnologyEnum = TechnologyEnum.INFLUENCE_TECHNOLOGY,
            override val cost: Int = 200,
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.SPACE_TRAVELLING),
            override val posX: Float = 0.4f,
            override val posY: Float = 0.4f
        ): SimpleTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return this.copy(state = newState)
            }
        }

        data class TradeTechnology(
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.tradeTechnologyNominative,
            override val descriptionId: Int = R.string.tradeTechnologyDescription,
            override val type: TechnologyEnum = TechnologyEnum.TRADE_TECHNOLOGY,
            override val cost: Int = 300,
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.INFLUENCE_TECHNOLOGY, TechnologyEnum.TRANSPORT_TECHNOLOGY),
            override val posX: Float = 0.55f,
            override val posY: Float = 0.4f
        ): SimpleTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return this.copy(state = newState)
            }
        }

        data class CruiserTechnology(
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.cruiserTechnologyNominative,
            override val descriptionId: Int = R.string.cruiserTechnologyDescription,
            override val type: TechnologyEnum = TechnologyEnum.CRUISER_TECHNOLOGY,
            override val cost: Int = 100,
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.SPACE_TRAVELLING),
            override val posX: Float = 0.4f,
            override val posY: Float = 0.1f
        ): SimpleTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return this.copy(state = newState)
            }
        }

        data class DestroyerTechnology(
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.destroyerTechnologyNominative,
            override val descriptionId: Int = R.string.destroyerTechnologyDescription,
            override val type: TechnologyEnum = TechnologyEnum.DESTROYER_TECHNOLOGY,
            override val cost: Int = 200,
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.CRUISER_TECHNOLOGY),
            override val posX: Float = 0.55f,
            override val posY: Float = 0.1f
            ): SimpleTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return this.copy(state = newState)
            }
        }

        data class GhostTechnology(
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.ghostTechnologyNominative,
            override val descriptionId: Int = R.string.ghostTechnologyDescription,
            override val type: TechnologyEnum = TechnologyEnum.GHOST_TECHNOLOGY,
            override val cost: Int = 300,
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.DESTROYER_TECHNOLOGY),
            override val posX: Float = 0.7f,
            override val posY: Float = 0.1f
        ): SimpleTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return this.copy(state = newState)
            }
        }

        data class WarperTechnology(
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.warperTechnologyNominative,
            override val descriptionId: Int = R.string.warperTechnologyDescription,
            override val type: TechnologyEnum = TechnologyEnum.WARPER_TECHNOLOGY,
            override val cost: Int = 400,
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.GHOST_TECHNOLOGY),
            override val posX: Float = 0.85f,
            override val posY: Float = 0.1f
        ): SimpleTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return this.copy(state = newState)
            }
        }
    }
    sealed class SynergyTechnology: Technology{
        abstract val bonusGainByDistrict: DistrictEnum
        abstract val bonusToResources: Map<Resource, Int>

        data class ProspectorSynergy(
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.prospectorSynergyNominative,
            override val descriptionId: Int = R.string.prospectorSynergyDescription,
            override val type: TechnologyEnum = TechnologyEnum.PROSPECTOR_SYNERGY,
            override val cost: Int = 200,
            override val bonusGainByDistrict: DistrictEnum = DistrictEnum.EMPTY,
            override val bonusToResources: Map<Resource, Int> = mapOf(
                Resource.METAL to 2,
                Resource.ORGANIC_SEDIMENTS to 1
            ),
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.ROCKET_SCIENCE),
            override val posX: Float = 0.25f,
            override val posY: Float = 1.0f
        ) : SynergyTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return this.copy(state = newState)
            }
        }

        data class IndustrialSynergy(
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.industrialSynergyNominative,
            override val descriptionId: Int = R.string.industrialSynergyDescription,
            override val type: TechnologyEnum = TechnologyEnum.INDUSTRIAL_SYNERGY,
            override val cost: Int = 500,
            override val bonusGainByDistrict: DistrictEnum = DistrictEnum.PROSPECTORS,
            override val bonusToResources: Map<Resource, Int> = mapOf(
                Resource.METAL to 2,
                Resource.ROCKET_MATERIALS to 2,
                Resource.INFRASTRUCTURE to 10
            ),
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.PROSPECTOR_SYNERGY, TechnologyEnum.SYNTHETIC_MATERIALS),
            override val posX: Float = 0.4f,
            override val posY: Float = 1.0f
        ): SynergyTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return this.copy(state = newState)
            }
        }

        data class ExpeditionSynergy(
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.expeditionSynergyNominative,
            override val descriptionId: Int = R.string.expeditionSynergyDescription,
            override val type: TechnologyEnum = TechnologyEnum.EXPEDITION_SYNERGY,
            override val cost: Int = 500,
            override val bonusGainByDistrict: DistrictEnum = DistrictEnum.INDUSTRIAL,
            override val bonusToResources: Map<Resource, Int> = mapOf(
                Resource.EXPEDITIONS to 2,
                Resource.ARMY to 2
            ),
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.INDUSTRIAL_SYNERGY, TechnologyEnum.TRANSPORT_TECHNOLOGY),
            override val posX: Float = 0.55f,
            override val posY: Float = 0.7f
        ): SynergyTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return this.copy(state = newState)
            }
        }

        data class UrbanCenterSynergy(
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.urbanCenterSynergyNominative,
            override val descriptionId: Int = R.string.urbanCenterSynergyDescription,
            override val type: TechnologyEnum = TechnologyEnum.URBAN_CENTER_SYNERGY,
            override val cost: Int = 500,
            override val bonusGainByDistrict: DistrictEnum = DistrictEnum.EMPTY,
            override val bonusToResources: Map<Resource, Int> = mapOf(
                Resource.RESEARCH to 2,
                Resource.INFLUENCE to 2
            ),
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.TRADE_TECHNOLOGY),
            override val posX: Float = 0.7f,
            override val posY: Float = 0.4f
        ): SynergyTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return this.copy(state = newState)
            }
        }
    }

    sealed class MultiplyTechnology: Technology{
        abstract val level: Int
        abstract val basicPrice: Int

        override val cost: Int
            get() = calculateCost()

        private fun calculateCost(): Int = basicPrice * (1 shl level)

        data class Cybernetics(
            override val level: Int = 0,
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.cyberneticsNominative,
            override val descriptionId: Int = R.string.cyberneticsDescription,
            override val type: TechnologyEnum = TechnologyEnum.CYBERNETICS,
            override val basicPrice: Int = 1000,
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.INDUSTRIAL_SYNERGY),
            override val posX: Float = 0.55f,
            override val posY: Float = 1.0f
        ): MultiplyTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return if(this.state == TechnologyResearchState.SELECTED && newState == TechnologyResearchState.FINISHED) return this.copy(level = this.level + 1)
                else this.copy(state = newState)
            }
        }

        data class Terraform(
            override val level: Int = 0,
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.terraformNominative,
            override val descriptionId: Int = R.string.terraformDescription,
            override val type: TechnologyEnum = TechnologyEnum.TERRAFORM,
            override val basicPrice: Int = 1000,
            val coefficient: Float = 0.8f,
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.EXPEDITION_SYNERGY),
            override val posX: Float = 0.7f,
            override val posY: Float = 0.7f
        ): MultiplyTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return if(this.state == TechnologyResearchState.SELECTED && newState == TechnologyResearchState.FINISHED) return this.copy(level = this.level + 1)
                else this.copy(state = newState)
            }
        }

        data class Diversity(
            override val level: Int = 0,
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.diversityNominative,
            override val descriptionId: Int = R.string.diversityDescription,
            override val type: TechnologyEnum = TechnologyEnum.DIVERSITY,
            override val basicPrice: Int = 1000,
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.URBAN_CENTER_SYNERGY),
            override val posX: Float = 0.85f,
            override val posY: Float = 0.4f
        ): MultiplyTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return if(this.state == TechnologyResearchState.SELECTED && newState == TechnologyResearchState.FINISHED) return this.copy(level = this.level + 1)
                else this.copy(state = newState)
            }
        }

        data class GravityManipulation(
            override val level: Int = 0,
            override val state: TechnologyResearchState = TechnologyResearchState.LOCKED,
            override val nameId: Int = R.string.gravityManipulationNominative,
            override val descriptionId: Int = R.string.gravityManipulationDescription,
            override val type: TechnologyEnum = TechnologyEnum.GRAVITY_MANIPULATION,
            override val basicPrice: Int = 5000,
            override val dependencies: List<TechnologyEnum> = listOf(
                TechnologyEnum.WARPER_TECHNOLOGY),
            override val posX: Float = 1.0f,
            override val posY: Float = 0.1f
        ): MultiplyTechnology(){
            override fun changeTechnologyState(newState: TechnologyResearchState): Technology {
                return if(this.state == TechnologyResearchState.SELECTED && newState == TechnologyResearchState.FINISHED) return this.copy(level = this.level + 1)
                else this.copy(state = newState)
            }
        }
    }
}

enum class TechnologyEnum(
    @StringRes val nameIdNominative: Int,
    @StringRes val nameIdGenitive: Int
){
    ROCKET_SCIENCE(R.string.rocketScienceNominative, R.string.rocketScienceGenitive),
    SYNTHETIC_MATERIALS(R.string.syntheticMaterialsNominative, R.string.syntheticMaterialsGenitive),
    SPACE_TRAVELLING(R.string.spaceTravellingNominative, R.string.spaceTravellingGenitive),
    TRANSPORT_TECHNOLOGY(R.string.transportTechnologyNominative, R.string.transportTechnologyGenitive),
    INFLUENCE_TECHNOLOGY(R.string.influenceTechnologyNominative, R.string.influenceTechnologyGenitive),
    TRADE_TECHNOLOGY(R.string.tradeTechnologyNominative, R.string.tradeTechnologyGenitive),
    CRUISER_TECHNOLOGY(R.string.cruiserTechnologyNominative, R.string.cruiserTechnologyGenitive),
    DESTROYER_TECHNOLOGY(R.string.destroyerTechnologyNominative, R.string.destroyerTechnologyGenitive),
    GHOST_TECHNOLOGY(R.string.ghostTechnologyNominative, R.string.ghostTechnologyGenitive),
    WARPER_TECHNOLOGY(R.string.warperTechnologyNominative, R.string.warperTechnologyGenitive),
    PROSPECTOR_SYNERGY(R.string.prospectorSynergyNominative, R.string.prospectorSynergyGenitive),
    INDUSTRIAL_SYNERGY(R.string.industrialSynergyNominative, R.string.industrialSynergyGenitive),
    EXPEDITION_SYNERGY(R.string.expeditionSynergyNominative, R.string.expeditionSynergyGenitive),
    URBAN_CENTER_SYNERGY(R.string.urbanCenterSynergyNominative, R.string.urbanCenterSynergyGenitive),
    CYBERNETICS(R.string.cyberneticsNominative, R.string.cyberneticsGenitive),
    TERRAFORM(R.string.terraformNominative, R.string.terraformGenitive),
    DIVERSITY(R.string.diversityNominative, R.string.diversityGenitive),
    GRAVITY_MANIPULATION(R.string.gravityManipulationNominative, R.string.gravityManipulationGenitive)
}

enum class TechnologyResearchState {
    SELECTED, FINISHED, UNLOCKED, LOCKED
}

fun String.toTechnologyResearchState(): TechnologyResearchState? {
    return try {
        TechnologyResearchState.valueOf(this)
    } catch (e: IllegalArgumentException) {
        Sentry.captureException(e)
        null
    }
}

fun String.toTechnologyEnum(): TechnologyEnum? {
    return try{
        TechnologyEnum.valueOf(this)
    } catch(e: IllegalArgumentException) {
        Sentry.captureException(e)
        null
    }
}

fun List<Technology>.toFirebaseMap(): Map<String, Map<String, Any>> {
    return associate { technology ->
        technology.type.name to technology.toMap()
    }
}

private fun technologyFromFirebaseMap(type: TechnologyEnum, data: Map<String, Any>): Technology? {
    return when (type) {
        // SimpleTechnology
        TechnologyEnum.ROCKET_SCIENCE -> Technology.SimpleTechnology.RocketScience(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
        )
        TechnologyEnum.SYNTHETIC_MATERIALS -> Technology.SimpleTechnology.SyntheticMaterials(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
        )
        TechnologyEnum.SPACE_TRAVELLING -> Technology.SimpleTechnology.SpaceTravelling(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
        )
        TechnologyEnum.TRANSPORT_TECHNOLOGY -> Technology.SimpleTechnology.TransportTechnology(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
        )
        TechnologyEnum.INFLUENCE_TECHNOLOGY -> Technology.SimpleTechnology.InfluenceTechnology(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
        )
        TechnologyEnum.TRADE_TECHNOLOGY -> Technology.SimpleTechnology.TradeTechnology(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
        )
        TechnologyEnum.CRUISER_TECHNOLOGY -> Technology.SimpleTechnology.CruiserTechnology(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
        )
        TechnologyEnum.DESTROYER_TECHNOLOGY -> Technology.SimpleTechnology.DestroyerTechnology(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
        )
        TechnologyEnum.GHOST_TECHNOLOGY -> Technology.SimpleTechnology.GhostTechnology(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
        )
        TechnologyEnum.WARPER_TECHNOLOGY -> Technology.SimpleTechnology.WarperTechnology(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
        )

        // SynergyTechnology
        TechnologyEnum.PROSPECTOR_SYNERGY -> Technology.SynergyTechnology.ProspectorSynergy(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
        )
        TechnologyEnum.INDUSTRIAL_SYNERGY -> Technology.SynergyTechnology.IndustrialSynergy(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
        )
        TechnologyEnum.EXPEDITION_SYNERGY -> Technology.SynergyTechnology.ExpeditionSynergy(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
        )
        TechnologyEnum.URBAN_CENTER_SYNERGY -> Technology.SynergyTechnology.UrbanCenterSynergy(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
        )

        // MultiplyTechnology
        TechnologyEnum.CYBERNETICS -> Technology.MultiplyTechnology.Cybernetics(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
            level = (data["level"] as? Long)?.toInt() ?: 0
        )
        TechnologyEnum.TERRAFORM -> Technology.MultiplyTechnology.Terraform(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
            level = (data["level"] as? Long)?.toInt() ?: 0
        )
        TechnologyEnum.DIVERSITY -> Technology.MultiplyTechnology.Diversity(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
            level = (data["level"] as? Long)?.toInt() ?: 0
        )
        TechnologyEnum.GRAVITY_MANIPULATION -> Technology.MultiplyTechnology.GravityManipulation(
            state = (data["state"] as? String)?.toTechnologyResearchState()?: TechnologyResearchState.LOCKED,
            level = (data["level"] as? Long)?.toInt() ?: 0
        )
    }
}

fun technologiesFromFirebaseMap(firebaseData: Map<String, Map<String, Any>>): List<Technology> {
    return firebaseData.mapNotNull { (typeName, data) ->
        val type = TechnologyEnum.valueOf(typeName)
        technologyFromFirebaseMap(type, data)
    }
}

fun createAllTechnologies(): List<Technology> {
    return listOf(
        // Simple Technologies
        Technology.SimpleTechnology.RocketScience(),
        Technology.SimpleTechnology.SyntheticMaterials(),
        Technology.SimpleTechnology.SpaceTravelling(),
        Technology.SimpleTechnology.TransportTechnology(),
        Technology.SimpleTechnology.InfluenceTechnology(),
        Technology.SimpleTechnology.TradeTechnology(),
        Technology.SimpleTechnology.CruiserTechnology(),
        Technology.SimpleTechnology.DestroyerTechnology(),
        Technology.SimpleTechnology.GhostTechnology(),
        Technology.SimpleTechnology.WarperTechnology(),

        // Synergy Technologies
        Technology.SynergyTechnology.ProspectorSynergy(),
        Technology.SynergyTechnology.IndustrialSynergy(),
        Technology.SynergyTechnology.ExpeditionSynergy(),
        Technology.SynergyTechnology.UrbanCenterSynergy(),

        // Multiply Technologies
        Technology.MultiplyTechnology.Cybernetics(),
        Technology.MultiplyTechnology.Terraform(),
        Technology.MultiplyTechnology.Diversity(),
        Technology.MultiplyTechnology.GravityManipulation()
    )
}