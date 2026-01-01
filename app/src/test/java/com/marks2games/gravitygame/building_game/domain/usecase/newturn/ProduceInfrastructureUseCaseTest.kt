package com.marks2games.gravitygame.building_game.domain.usecase.newturn

import com.marks2games.gravitygame.building_game.data.model.District
import com.marks2games.gravitygame.building_game.data.model.IndustrialMode
import com.marks2games.gravitygame.building_game.data.model.InfrastructureSetting
import com.marks2games.gravitygame.building_game.data.model.Planet
import com.marks2games.gravitygame.building_game.data.model.Resource
import com.marks2games.gravitygame.building_game.data.model.ResourceChange
import com.marks2games.gravitygame.building_game.data.model.SmallPlanet
import com.marks2games.gravitygame.building_game.domain.usecase.newturn.utils.CalculatePlanetMaintenanceUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.technology.ApplyDiversityTechnologyUseCase
import com.marks2games.gravitygame.building_game.domain.usecase.technology.ApplySynergyUseCase
import com.marks2games.gravitygame.core.domain.error.ProduceInfraResult
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ProduceInfrastructureUseCaseTest {

    private lateinit var maintenanceCost: CalculatePlanetMaintenanceUseCase
    private lateinit var applyDiversity: ApplyDiversityTechnologyUseCase
    private lateinit var applySynergy: ApplySynergyUseCase
    private lateinit var useCase: ProduceInfrastructureUseCase

    @Before
    fun setUp() {
        maintenanceCost = mockk()
        applyDiversity = mockk()
        applySynergy = mockk()
        useCase = ProduceInfrastructureUseCase(maintenanceCost, applyDiversity, applySynergy)
    }

    private fun createCapitolDistrict(infraProduced: Int): District.Capitol {
        return mockk(relaxed = true) {
            every { generateResources() } returns ResourceChange(
                produced = mapOf(Resource.INFRASTRUCTURE to infraProduced),
                consumed = emptyMap()
            )
        }
    }

    private fun createIndustrialDistrict(
        districtId: Int,
        baseInfra: Int,
        metalConsumed: Int,
        isWorking: Boolean = true
    ): District.Industrial {
        return mockk {
            every { this@mockk.mode } returns IndustrialMode.INFRASTRUCTURE
            every { this@mockk.isWorking } returns isWorking
            every { this@mockk.districtId } returns districtId
            every { generateResources() } returns ResourceChange(
                produced = mapOf(Resource.INFRASTRUCTURE to baseInfra),
                consumed = mapOf(Resource.METAL to metalConsumed)
            )
        }
    }

    @Test
    fun `produces infrastructure only from capitol`() {
        val planet = Planet(
            districts = listOf(createCapitolDistrict(3)),
            planetMetal = 10,
            metal = 10,
            infrastructureSetting = InfrastructureSetting.NOTHING,
            level = 1,
            progressSetting = 0,
            type = SmallPlanet
        )

        every { maintenanceCost.invoke(any()) } returns 0
        every { applyDiversity(any(), any(), any()) } returns 1f
        every { applySynergy(any(), any(), any(), any(), any()) } returns 0

        val result = useCase(planet, emptyList(), isPlanning = false, technologies = emptyList())

        assertTrue(result is ProduceInfraResult.Success)
        result as ProduceInfraResult.Success
        assertEquals(3, result.newInfra)
        assertEquals(10, result.newMetal)
        assertEquals(7, result.newPlanetMetal)
    }

    @Test
    fun `produces infrastructure with synergy and diversity`() {
        val capitol = createCapitolDistrict(2)
        val industrial = createIndustrialDistrict(1, 5, 1)

        val planet = Planet(
            districts = listOf(capitol, industrial),
            planetMetal = 10,
            metal = 10,
            infrastructureSetting = InfrastructureSetting.MAXIMUM,
            level = 1,
            progressSetting = 0,
            type = SmallPlanet
        )

        every { maintenanceCost.invoke(any()) } returns 0
        every { applyDiversity(any(), any(), any()) } returns 1.5f
        every { applySynergy(any(), any(), any(), any(), any()) } returns 3

        val result = useCase(planet, emptyList(), isPlanning = false, technologies = emptyList())

        assertTrue(result is ProduceInfraResult.Success)
        result as ProduceInfraResult.Success
        assertEquals(14, result.newInfra) // (5+3)*1.5 + 2 = 14
    }

    @Test
    fun `returns FailureWithSuccess when not enough infra and planning`() {
        val capitol = createCapitolDistrict(1)
        val industrial = createIndustrialDistrict(1, 5, 1)

        val planet = Planet(
            districts = listOf(capitol, industrial),
            planetMetal = 10,
            metal = 10,
            infrastructureSetting = InfrastructureSetting.USAGE,
            level = 1,
            progressSetting = 10,
            type = SmallPlanet
        )

        every { maintenanceCost.invoke(any()) } returns 2
        every { applyDiversity(any(), any(), any()) } returns 1f
        every { applySynergy(any(), any(), any(), any(), any()) } returns 0

        val result = useCase(planet, emptyList(), isPlanning = true, technologies = emptyList())

        assertTrue(result is ProduceInfraResult.FailureWihSuccess)
        result as ProduceInfraResult.FailureWihSuccess
        assertTrue(true)
        assertTrue(result.error is ProduceInfraResult.Error.MissingInfra)
    }

    @Test
    fun `returns Failure when not enough infra and not planning`() {
        val capitol = createCapitolDistrict(1)
        val industrial = createIndustrialDistrict(1, 5, 1)

        val planet = Planet(
            districts = listOf(capitol, industrial),
            planetMetal = 10,
            metal = 10,
            infrastructureSetting = InfrastructureSetting.USAGE,
            level = 1,
            progressSetting = 10,
            type = SmallPlanet
        )

        every { maintenanceCost.invoke(any()) } returns 2
        every { applyDiversity(any(), any(), any()) } returns 1f
        every { applySynergy(any(), any(), any(), any(), any()) } returns 0

        val result = useCase(planet, emptyList(), isPlanning = false, technologies = emptyList())

        assertTrue(result is ProduceInfraResult.Error.MissingInfra)
    }
}
