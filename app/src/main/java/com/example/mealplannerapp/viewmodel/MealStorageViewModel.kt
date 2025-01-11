package com.example.mealplannerapp.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplannerapp.data.MealDao
import com.example.mealplannerapp.data.MealEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MealStorageViewModel(private val mealDao: MealDao) : ViewModel() {

    private val _mealsByDay = MutableStateFlow<List<MealEntry>>(emptyList())
    val mealsByDay: StateFlow<List<MealEntry>> = _mealsByDay

    fun fetchMealsByDayForUser(day: String, userId: String) {
        viewModelScope.launch {
            _mealsByDay.value = mealDao.getMealsByDayForUser(day,userId)
        }
    }

    fun deleteMeal(id: Int) {
        viewModelScope.launch {
            mealDao.deleteMeal(id)
        }
    }

    fun saveMeal(dayOfWeek: String, title: String, mealApiId: Int, imageUrl: String, calories: Double, userId: String) {
        viewModelScope.launch {
            val meal = MealEntry(
                dayOfWeek = dayOfWeek,
                title = title,
                mealApiId = mealApiId, // Yeni mealApiId parametresi burada geçiliyor
                imageUrl = imageUrl,
                calories = calories,
                userId = userId
            )
            mealDao.insertMeal(meal)
        }
    }
}