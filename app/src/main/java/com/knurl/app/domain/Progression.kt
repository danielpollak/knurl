package com.knurl.app.domain

/** The rules that turn a completed (or stale) exercise into its next working weight. */
object Progression {

    const val STALENESS_DAYS_THRESHOLD = 14
    const val FAIL_STREAK_DELOAD_THRESHOLD = 3

    /**
     * True if this exercise hasn't been performed in over [STALENESS_DAYS_THRESHOLD] days
     * and should be deloaded before the upcoming session starts.
     */
    fun isStale(lastPerformedEpochDate: Long?, todayEpochDate: Long): Boolean {
        if (lastPerformedEpochDate == null) return false
        return todayEpochDate - lastPerformedEpochDate > STALENESS_DAYS_THRESHOLD
    }

    /**
     * The weight and fail streak to start a session with: deloaded if the exercise is stale,
     * otherwise unchanged. Does not persist anything -- callers decide when/whether to save it.
     */
    fun startingWeight(
        storedWeight: Double,
        storedFailStreak: Int,
        lastPerformedEpochDate: Long?,
        todayEpochDate: Long,
        barWeight: Double,
        availablePlates: List<Double>
    ): WorkingWeightState {
        return if (isStale(lastPerformedEpochDate, todayEpochDate)) {
            WorkingWeightState(
                weight = WeightMath.deload(storedWeight, barWeight, availablePlates),
                failStreak = 0
            )
        } else {
            WorkingWeightState(weight = storedWeight, failStreak = storedFailStreak)
        }
    }

    /**
     * Applies the success/fail rule for one exercise given the weight/fail streak it started
     * the session with (see [startingWeight]) and whether every set hit its target reps.
     */
    fun applyResult(
        startingWeight: WorkingWeightState,
        succeeded: Boolean,
        increment: Double,
        barWeight: Double,
        availablePlates: List<Double>
    ): WorkingWeightState {
        return if (succeeded) {
            WorkingWeightState(weight = startingWeight.weight + increment, failStreak = 0)
        } else {
            val failStreak = startingWeight.failStreak + 1
            if (failStreak >= FAIL_STREAK_DELOAD_THRESHOLD) {
                WorkingWeightState(
                    weight = WeightMath.deload(startingWeight.weight, barWeight, availablePlates),
                    failStreak = 0
                )
            } else {
                WorkingWeightState(weight = startingWeight.weight, failStreak = failStreak)
            }
        }
    }
}

data class WorkingWeightState(val weight: Double, val failStreak: Int)
