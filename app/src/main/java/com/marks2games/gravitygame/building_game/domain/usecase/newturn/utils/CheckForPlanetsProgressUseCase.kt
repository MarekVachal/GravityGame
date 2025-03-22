package com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils

import com.marks2games.gravitygame.building_game.data.model.DistrictEnum
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.BuildDistrictUseCase
import com.marks2games.gravitygame.core.domain.error.BuildDistrictResult
import javax.inject.Inject

/**
 * Use case responsible for checking and updating the progress of a planet.
 *
 * This use case checks if the planet's progress has reached
 * a threshold based on its current level. If a planet's progress exceeds the threshold,
 * it levels up, its progress is adjusted, and new district is added to it.
 *
 * @property buildDistrictUseCase Use case for building new districts on a planet.
 */
class CheckForPlanetsProgressUseCase @Inject constructor(
    private val buildDistrictUseCase: BuildDistrictUseCase,
    private val getIdForNewDistrictUseCase: GetIdForNewDistrictUseCase
) {
    /**
     * Update a planet based on its progress and level.
     *
     * This function checks if a planet's progress has reached or exceeded a threshold (level * 10).
     * If it has, the planet's level is incremented, its progress is reduced by the threshold,
     * and new district is generated using the [buildDistrictUseCase].
     *
     * @param planet The [Planet] objects to update.
     * @return A [Planet] objects with updated level, progress, and districts,
     *         if applicable. If the planet did not meet the progress threshold is returned unchanged.
     *
     * @see Planet
     * @see DistrictEnum
     * @see BuildDistrictUseCase
     */
    operator fun invoke(planet: Planet): Pair<Planet, BuildDistrictResult.Error?> {
        var updatedPlanet = planet
        var error: BuildDistrictResult.Error? = null
        if (planet.progress >= planet.level * 10) {
            val buildDistrictResult =
                buildDistrictUseCase.invoke(planet, DistrictEnum.EMPTY, getIdForNewDistrictUseCase.invoke(planet.districts.count()))
            when (buildDistrictResult) {
                is BuildDistrictResult.Success -> {
                    updatedPlanet = planet.copy(
                        level = planet.level + 1,
                        progress = planet.progress - planet.level * 10,
                        districts = buildDistrictResult.districts
                    )
                }
                is BuildDistrictResult.Error -> {
                    error = buildDistrictResult
                }
            }
        }
        return Pair(updatedPlanet, error)

    }
}