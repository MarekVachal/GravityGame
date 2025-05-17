package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import android.util.Log
import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import javax.inject.Inject
import kotlin.math.min

/**
 * Use case responsible for producing army units on a given planet.
 *
 * This class calculates the number of army units that can be produced based on the planet's
 * available resources (rocket materials), the production and consumption rates of the Expedition
 * Platform district, and the planet's army construction setting.
 *
 * @constructor Creates a ProduceArmyUnitUseCase instance. No dependencies are injected in this
 *              specific example, but the `@Inject constructor()` allows for potential dependency
 *              injection in a larger application.
 */
class ProduceArmyUnitUseCase @Inject constructor() {
    /**
     * Simulates the production of an army on a given planet, considering resource constraints and settings.
     *
     * This function calculates how many army units can be produced on a specific [planet]
     * based on the available rocket materials, the production and consumption rates of resources
     * in the Expedition Platform district, and the planet's army construction setting.
     *
     * @param planet The [Planet] where the army is being produced. It contains information about
     *               available rocket materials and the maximum number of armies that can be built concurrently.
     * @return A [Pair] where:
     *         - The first element (Int) represents the number of army units that can be produced.
     *         - The second element (Int) represents the remaining amount of rocket materials after production.
     *
     * @throws NoSuchElementException if the production or consumption rates are missing in the generated resources. This will happen if the Resource.ARMY or Resource.ROCKET_MATERIALS is not listed in the District.ExpeditionPlatform.generateResources results.
     *
     * Example:
     * val planet = Planet(rocketMaterials = 100, armyConstructionSetting = 50)
     * val result = invoke(planet)
     * println("Produced Armies: ${result.first}, Remaining Rocket Materials: ${result.second}")
     * ```
     */
    operator fun invoke(
        planet: Planet,
    ): Pair<Int, Int> {
        val district = planet.districts
            .filterIsInstance<District.ExpeditionPlatform>()
            .firstOrNull() ?: return Pair(planet.army, planet.rocketMaterials)
        val productionRate = district.generateResources().produced[Resource.ARMY] ?: 0
        val consumptionRate = district.generateResources().consumed[Resource.ARMY] ?: 0
        val maxPossibleProductionOnResource = (planet.rocketMaterials.toDouble() / consumptionRate * productionRate).toInt()
        Log.d("ProduceArmyUnitUseCase", "maxPossibleProductionOnResource: $maxPossibleProductionOnResource")
        val maxPossibleProduction = min(maxPossibleProductionOnResource, planet.rocketMaterials)
        Log.d("ProduceArmyUnitUseCase", "maxPossibleProduction: $maxPossibleProduction")
        val availableProduction = min(maxPossibleProduction, planet.armyConstructionSetting)
        Log.d("ProduceArmyUnitUseCase", "availableProduction: $availableProduction")
        val newArmy = planet.army + availableProduction
        Log.d("ProduceArmyUnitUseCase", "newArmy: $newArmy")
        val newRocketMaterials = planet.rocketMaterials - availableProduction
        Log.d("ProduceArmyUnitUseCase", "newRocketMaterials: $newRocketMaterials")
        return Pair(newArmy, newRocketMaterials)
    }
}