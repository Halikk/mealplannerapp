package com.example.mealplannerapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_entries")
data class MealEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val dayOfWeek: String,
    val title: String,
    val mealApiId: Int,
    val imageUrl: String,
    val calories: Double,
    val userId: String

)