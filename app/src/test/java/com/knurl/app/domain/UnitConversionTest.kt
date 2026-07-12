package com.knurl.app.domain

import com.knurl.app.data.model.WeightUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class UnitConversionTest {

    @Test
    fun `convert is a no-op for the same unit`() {
        assertEquals(135.0, UnitConversion.convert(135.0, WeightUnit.LB, WeightUnit.LB), 0.0)
    }

    @Test
    fun `convert lb to kg`() {
        assertEquals(61.23, UnitConversion.convert(135.0, WeightUnit.LB, WeightUnit.KG), 0.01)
    }

    @Test
    fun `convert kg to lb`() {
        assertEquals(135.0, UnitConversion.convert(61.23, WeightUnit.KG, WeightUnit.LB), 0.1)
    }

    @Test
    fun `default bar and plates differ by unit`() {
        assertEquals(45.0, UnitConversion.defaultBarWeight(WeightUnit.LB), 0.0)
        assertEquals(20.0, UnitConversion.defaultBarWeight(WeightUnit.KG), 0.0)
        assertEquals(listOf(45.0, 35.0, 25.0, 10.0, 5.0, 2.5), UnitConversion.defaultPlates(WeightUnit.LB))
        assertEquals(listOf(25.0, 20.0, 15.0, 10.0, 5.0, 2.5, 1.25), UnitConversion.defaultPlates(WeightUnit.KG))
    }

    @Test
    fun `roundToHalf rounds to the nearest half`() {
        assertEquals(61.5, UnitConversion.roundToHalf(61.3), 0.0)
        assertEquals(61.0, UnitConversion.roundToHalf(61.2), 0.0)
    }
}
