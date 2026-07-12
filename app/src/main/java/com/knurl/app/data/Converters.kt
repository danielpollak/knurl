package com.knurl.app.data

import androidx.room.TypeConverter
import com.knurl.app.data.model.MuscleGroup
import com.knurl.app.data.model.ProgramType
import com.knurl.app.data.model.WeightUnit

class Converters {
    @TypeConverter
    fun fromMuscleGroup(value: MuscleGroup): String = value.name

    @TypeConverter
    fun toMuscleGroup(value: String): MuscleGroup = MuscleGroup.valueOf(value)

    @TypeConverter
    fun fromProgramType(value: ProgramType): String = value.name

    @TypeConverter
    fun toProgramType(value: String): ProgramType = ProgramType.valueOf(value)

    @TypeConverter
    fun fromWeightUnit(value: WeightUnit): String = value.name

    @TypeConverter
    fun toWeightUnit(value: String): WeightUnit = WeightUnit.valueOf(value)

    @TypeConverter
    fun fromPlatesList(value: List<Double>): String = value.joinToString(",")

    @TypeConverter
    fun toPlatesList(value: String): List<Double> =
        if (value.isBlank()) emptyList() else value.split(",").map { it.toDouble() }
}
