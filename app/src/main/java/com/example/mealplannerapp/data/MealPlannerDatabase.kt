package com.example.mealplannerapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MealEntry::class], version = 3, exportSchema = true)
abstract class MealPlannerDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao

    companion object {
        @Volatile
        private var INSTANCE: MealPlannerDatabase? = null

        fun getInstance(context: Context): MealPlannerDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MealPlannerDatabase::class.java,
                    "meal_planner_database"
                )
                    .fallbackToDestructiveMigration() // Eski verileri silerek yeniden oluşturur
                    .build().also { INSTANCE = it }
            }
        }
    }
}