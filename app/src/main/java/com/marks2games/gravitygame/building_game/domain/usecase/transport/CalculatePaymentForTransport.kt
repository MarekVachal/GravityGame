package com.marks2games.gravitygame.building_game.domain.usecase.transport

import com.marks2games.gravitygame.building_game.data.model.Transport
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max

class CalculatePaymentForTransport @Inject constructor() {
    operator fun invoke(transport: Transport): Float {
        val amountExport = transport.exportFromPlanet1.values.sum()
        val amountImport = transport.exportFromPlanet2.values.sum()
        val amount = max(amountExport, amountImport).toFloat()
        return if (amount > 0f) ceil(amount / 10f) else 0f
    }
}