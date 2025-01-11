package com.example.mealplannerapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(mealEntry: MealEntry)

    @Query("SELECT * FROM meal_entries WHERE dayOfWeek = :day AND userId = :userId")
    suspend fun getMealsByDayForUser(day: String, userId: String): List<MealEntry> // Güne göre yemekleri getir

    @Query("SELECT * FROM meal_entries WHERE id = :id")
    suspend fun getMealById(id: Int): MealEntry? // ID ile yemek detaylarını getir

    @Query("DELETE FROM meal_entries WHERE id = :id")
    suspend fun deleteMeal(id: Int) // ID'ye göre yemek sil
}