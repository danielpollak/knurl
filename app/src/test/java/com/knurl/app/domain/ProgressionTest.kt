package com.knurl.app.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressionTest {

    private val lbPlates = listOf(45.0, 35.0, 25.0, 10.0, 5.0, 2.5)
    private val barWeight = 45.0

    @Test
    fun `isStale is false with no prior session`() {
        assertFalse(Progression.isStale(null, todayEpochDate = 100L))
    }

    @Test
    fun `isStale is false exactly at the 14 day threshold`() {
        assertFalse(Progression.isStale(lastPerformedEpochDate = 86L, todayEpochDate = 100L))
    }

    @Test
    fun `isStale is true just past the 14 day threshold`() {
        assertTrue(Progression.isStale(lastPerformedEpochDate = 85L, todayEpochDate = 100L))
    }

    @Test
    fun `startingWeight is unchanged when not stale`() {
        val result = Progression.startingWeight(
            storedWeight = 135.0,
            storedFailStreak = 1,
            lastPerformedEpochDate = 99L,
            todayEpochDate = 100L,
            barWeight = barWeight,
            availablePlates = lbPlates
        )
        assertEquals(135.0, result.weight, 0.0)
        assertEquals(1, result.failStreak)
    }

    @Test
    fun `startingWeight deloads and resets fail streak when stale`() {
        val result = Progression.startingWeight(
            storedWeight = 135.0,
            storedFailStreak = 2,
            lastPerformedEpochDate = 50L,
            todayEpochDate = 100L,
            barWeight = barWeight,
            availablePlates = lbPlates
        )
        assertEquals(WeightMath.deload(135.0, barWeight, lbPlates), result.weight, 0.0)
        assertEquals(0, result.failStreak)
    }

    @Test
    fun `applyResult on success adds the increment and resets fail streak`() {
        val start = WorkingWeightState(weight = 135.0, failStreak = 1)
        val result = Progression.applyResult(start, succeeded = true, increment = 5.0, barWeight, lbPlates)
        assertEquals(140.0, result.weight, 0.0)
        assertEquals(0, result.failStreak)
    }

    @Test
    fun `applyResult on failure keeps weight and increments fail streak`() {
        val start = WorkingWeightState(weight = 135.0, failStreak = 0)
        val result = Progression.applyResult(start, succeeded = false, increment = 5.0, barWeight, lbPlates)
        assertEquals(135.0, result.weight, 0.0)
        assertEquals(1, result.failStreak)
    }

    @Test
    fun `applyResult deloads on the third consecutive failure`() {
        val start = WorkingWeightState(weight = 135.0, failStreak = 2)
        val result = Progression.applyResult(start, succeeded = false, increment = 5.0, barWeight, lbPlates)
        assertEquals(WeightMath.deload(135.0, barWeight, lbPlates), result.weight, 0.0)
        assertEquals(0, result.failStreak)
    }
}
