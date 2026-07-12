package com.knurl.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.knurl.app.data.dao.ExerciseDao
import com.knurl.app.data.dao.ProgramDao
import com.knurl.app.data.dao.ProgramDayDao
import com.knurl.app.data.dao.ProgramExerciseDao
import com.knurl.app.data.dao.SetLogDao
import com.knurl.app.data.dao.SettingsDao
import com.knurl.app.data.dao.WorkingWeightDao
import com.knurl.app.data.dao.WorkoutSessionDao
import com.knurl.app.data.entity.Exercise
import com.knurl.app.data.entity.Program
import com.knurl.app.data.entity.ProgramDay
import com.knurl.app.data.entity.ProgramExercise
import com.knurl.app.data.entity.SetLog
import com.knurl.app.data.entity.Settings
import com.knurl.app.data.entity.WorkingWeight
import com.knurl.app.data.entity.WorkoutSession
import com.knurl.app.data.seed.SeedData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        Exercise::class,
        Program::class,
        ProgramDay::class,
        ProgramExercise::class,
        WorkingWeight::class,
        WorkoutSession::class,
        SetLog::class,
        Settings::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun programDao(): ProgramDao
    abstract fun programDayDao(): ProgramDayDao
    abstract fun programExerciseDao(): ProgramExerciseDao
    abstract fun workingWeightDao(): WorkingWeightDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun setLogDao(): SetLogDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, applicationScope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "knurl.db"
                )
                    .addCallback(SeedCallback(applicationScope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SeedCallback(private val applicationScope: CoroutineScope) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                applicationScope.launch { seedDatabase(database) }
            }
        }
    }
}

internal suspend fun seedDatabase(database: AppDatabase) {
    val exerciseDao = database.exerciseDao()
    val programDao = database.programDao()
    val programDayDao = database.programDayDao()
    val programExerciseDao = database.programExerciseDao()
    val workingWeightDao = database.workingWeightDao()
    val settingsDao = database.settingsDao()

    settingsDao.upsert(SeedData.defaultSettings())

    val exerciseIds = exerciseDao.insertAll(SeedData.libraryExercises)
    val exerciseIdByName = SeedData.libraryExercises.map { it.name }.zip(exerciseIds).toMap()

    val programId = programDao.insert(SeedData.strongLiftsProgram())
    val (dayAId, dayBId) = programDayDao.insertAll(SeedData.strongLiftsDays(programId))

    programExerciseDao.insertAll(
        SeedData.strongLiftsExercises(dayAId, dayBId, exerciseIdByName)
    )
    workingWeightDao.upsertAll(
        SeedData.strongLiftsWorkingWeights(programId, exerciseIdByName)
    )
}
