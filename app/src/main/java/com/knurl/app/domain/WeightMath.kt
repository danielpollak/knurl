package com.knurl.app.domain

import kotlin.math.floor

/** Pure weight/plate math shared by the workout screen and progression rules. */
object WeightMath {

    /**
     * Rounds [weight] down to the nearest weight that can actually be loaded on the bar
     * with the given [barWeight] and [availablePlates], assuming plates are added in pairs.
     * The finest achievable increment is twice the smallest available plate.
     */
    fun roundDownToLoadable(weight: Double, barWeight: Double, availablePlates: List<Double>): Double {
        val smallestPlate = availablePlates.minOrNull()
        if (smallestPlate == null || smallestPlate <= 0.0) return weight
        val perSide = (weight - barWeight) / 2.0
        if (perSide <= 0.0) return barWeight
        val roundedPerSide = floor(perSide / smallestPlate) * smallestPlate
        return barWeight + 2.0 * roundedPerSide
    }

    /** Greedily picks plates for one side of the bar, largest first. Assumes ample supply of each. */
    fun perSidePlates(weight: Double, barWeight: Double, availablePlates: List<Double>): List<Double> {
        var remaining = (weight - barWeight) / 2.0
        if (remaining <= 0.0) return emptyList()

        val plates = mutableListOf<Double>()
        val descending = availablePlates.sortedDescending()
        val epsilon = 1e-6
        for (plate in descending) {
            if (plate <= 0.0) continue
            while (remaining + epsilon >= plate) {
                plates.add(plate)
                remaining -= plate
            }
        }
        return plates
    }

    /** Deloads [weight] by 10%, rounded down to the nearest loadable weight. */
    fun deload(weight: Double, barWeight: Double, availablePlates: List<Double>): Double =
        roundDownToLoadable(weight * 0.9, barWeight, availablePlates)
}
