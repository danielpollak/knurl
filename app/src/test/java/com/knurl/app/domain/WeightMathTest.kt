package com.knurl.app.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class WeightMathTest {

    private val lbPlates = listOf(45.0, 35.0, 25.0, 10.0, 5.0, 2.5)
    private val barWeight = 45.0

    @Test
    fun `roundDownToLoadable keeps an already-loadable weight unchanged`() {
        assertEquals(135.0, WeightMath.roundDownToLoadable(135.0, barWeight, lbPlates), 0.0)
    }

    @Test
    fun `roundDownToLoadable rounds down to the nearest 2x smallest plate`() {
        // 126 lb: per side is 40.5, floors to 40 (smallest plate 2.5) -> 45 + 2*40 = 125.
        assertEquals(125.0, WeightMath.roundDownToLoadable(126.0, barWeight, lbPlates), 0.0)
    }

    @Test
    fun `roundDownToLoadable never goes below the bar`() {
        assertEquals(barWeight, WeightMath.roundDownToLoadable(40.0, barWeight, lbPlates), 0.0)
    }

    @Test
    fun `perSidePlates picks largest plates first`() {
        // 135 lb: (135-45)/2 = 45 per side => one 45.
        assertEquals(listOf(45.0), WeightMath.perSidePlates(135.0, barWeight, lbPlates))
    }

    @Test
    fun `perSidePlates combines multiple denominations`() {
        // 185 lb: (185-45)/2 = 70 per side => 45 + 25.
        assertEquals(listOf(45.0, 25.0), WeightMath.perSidePlates(185.0, barWeight, lbPlates))
    }

    @Test
    fun `perSidePlates is empty at bar weight`() {
        assertEquals(emptyList<Double>(), WeightMath.perSidePlates(45.0, barWeight, lbPlates))
    }

    @Test
    fun `deload reduces by 10 percent and rounds down`() {
        // 135 * 0.9 = 121.5; per side 38.25, floors to 37.5 -> weight = 45 + 75 = 120.
        assertEquals(120.0, WeightMath.deload(135.0, barWeight, lbPlates), 0.0)
    }
}
