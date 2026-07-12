package com.knurl.app.domain

import com.knurl.app.data.model.WeightUnit
import kotlin.math.round

/** lb/kg conversion plus each unit's standard default bar and plate set. */
object UnitConversion {

    private const val LB_PER_KG = 2.2046226218

    fun convert(value: Double, from: WeightUnit, to: WeightUnit): Double = when {
        from == to -> value
        from == WeightUnit.LB && to == WeightUnit.KG -> value / LB_PER_KG
        else -> value * LB_PER_KG
    }

    fun defaultBarWeight(unit: WeightUnit): Double = if (unit == WeightUnit.LB) 45.0 else 20.0

    fun defaultPlates(unit: WeightUnit): List<Double> = if (unit == WeightUnit.LB) {
        listOf(45.0, 35.0, 25.0, 10.0, 5.0, 2.5)
    } else {
        listOf(25.0, 20.0, 15.0, 10.0, 5.0, 2.5, 1.25)
    }

    /** Cosmetic rounding for historical records, which don't need to land on a loadable weight. */
    fun roundToHalf(value: Double): Double = round(value * 2) / 2.0
}
