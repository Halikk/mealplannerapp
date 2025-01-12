package com.example.mealplannerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplannerapp.data.MealDao
import com.example.mealplannerapp.data.MealEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MealStorageViewModel(private val mealDao: MealDao) : ViewModel() {

    // Günlere göre yemekleri tutan MutableStateFlow. Dinamik olarak güncellenir.
    private val _mealsByDay = MutableStateFlow<List<MealEntry>>(emptyList())
    val mealsByDay: StateFlow<List<MealEntry>> = _mealsByDay // Dışarıya yalnızca okunabilir şekilde sunulur.

    // Belirli bir gün ve kullanıcı için yemekleri getirir.
    fun fetchMealsByDayForUser(day: String, userId: String) {
        viewModelScope.launch {
            // Veritabanından yemekleri al ve _mealsByDay'e ata.
            _mealsByDay.value = mealDao.getMealsByDayForUser(day, userId)
        }
    }

    // Verilen ID'ye sahip bir yemeği veritabanından siler.
    fun deleteMeal(id: Int) {
        viewModelScope.launch {
            mealDao.deleteMeal(id) // Veritabanından yemek silme işlemi
        }
    }

    // Yeni bir yemek kaydı oluşturur ve veritabanına ekler.
    fun saveMeal(dayOfWeek: String, title: String, mealApiId: Int, imageUrl: String, calories: Double, userId: String) {
        viewModelScope.launch {
            // Yeni bir MealEntry oluştur
            val meal = MealEntry(
                dayOfWeek = dayOfWeek, // Yemek günü
                title = title, // Yemek başlığı
                mealApiId = mealApiId, // Yemek API ID'si
                imageUrl = imageUrl, // Yemek görsel URL'si
                calories = calories, // Yemek kalorisi
                userId = userId // Kullanıcı ID'si
            )
            mealDao.insertMeal(meal) // Veritabanına ekle
        }
    }
}