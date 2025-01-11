package com.example.mealplannerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealplannerapp.data.Meal
import com.example.mealplannerapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealViewModel : ViewModel() {

    var age: Int = 25
    var height: Int = 170         // cm
    var weight: Double = 70.0     // kg
    var gender: String = "Male"

    private val _dailyCalories = MutableStateFlow(2000)
    val dailyCaloriesFlow = _dailyCalories.asStateFlow()
    private val _mealsFlow = MutableStateFlow<List<Meal>>(emptyList())
    val mealsFlow = _mealsFlow.asStateFlow()

    // Daha önce gelen tarif ID'lerini tutarak tekrar gelmesini engellemek
    private val usedRecipeIds = mutableSetOf<Int>()

    // Her “Fetch”’te bu fonksiyon çalışsın
    fun fetchThreeMealsSeparately() {
        viewModelScope.launch {
            try {
                // 1) Günlük kalori hesapla (varsa sonradan da tetiklenebilir)
                calculateDailyCalories()

                // 2) Sabah, öğle, akşam kalori paylaştır
                val breakfastCals = (dailyCaloriesFlow.value * 0.25).toInt()
                val lunchCals = (dailyCaloriesFlow.value * 0.35).toInt()
                val dinnerCals = (dailyCaloriesFlow.value * 0.40).toInt()

                // 3) 3 ayrı istek yap (Sequential veya parallel).
                val breakfastMeal = fetchOneMealOfType("breakfast", breakfastCals + 150)
                val lunchMeal = fetchOneMealOfType("lunch", lunchCals + 150)
                val dinnerMeal = fetchOneMealOfType("main course", dinnerCals + 150)

                // 4) Filter out null
                val finalMeals = listOfNotNull(breakfastMeal, lunchMeal, dinnerMeal)

                _mealsFlow.value = finalMeals

            } catch (e: Exception) {
                e.printStackTrace()
                _mealsFlow.value = emptyList()
            }
        }
    }

    // Mifflin-St Jeor
    fun calculateDailyCalories() {
        val bmr = if (gender == "Male") {
            10 * weight + 6.25 * height - 5 * age + 5
        } else {
            10 * weight + 6.25 * height - 5 * age - 161
        }
        // Sedanter => 1.2 çarpan
        _dailyCalories.value = (bmr * 1.2).toInt()
    }

    // Tek öğün getiren fonksiyon
    // number=10 tarif çekiyor -> bu 10 tarifin içinden, "usedRecipeIds"te olmayan
    // en yüksek kalorili (veya ilk bulduğumuz) tarifi seçiyor.
    private suspend fun fetchOneMealOfType(type: String, maxCals: Int): Meal? {
        val response = RetrofitClient.api.getMealsByType(
            apiKey = "23669ef999af4ddba191f61b71f6f9f7",
            type = type,
            maxCalories = maxCals,
            number = 10,
            includeNutrition = true
        )
        if (!response.isSuccessful || response.body() == null) return null

        val recipes = response.body()!!.results ?: emptyList()
        // Tüm tarifleri Meal tipine dönüştür
        val mealList = recipes.mapNotNull { recipe ->
            val cals = recipe.nutrition?.nutrients?.firstOrNull { it.name == "Calories" }?.amount
            if (recipe.id != null && recipe.title != null && recipe.image != null && cals != null) {
                Meal(
                    id = recipe.id,
                    title = recipe.title,
                    image = recipe.image,
                    calories = cals
                )
            } else null
        }

        // "usedRecipeIds" set'inde **olmayan** tarifleri al
        val newMeals = mealList.filter { !usedRecipeIds.contains(it.id) }
        if (newMeals.isEmpty()) return null

        // 1 tarif seçelim (ör: en yüksek kalorili)
        val chosen = newMeals.maxByOrNull { it.calories }
        if (chosen != null) {
            usedRecipeIds.add(chosen.id) // birdaha gelmesin
        }
        return chosen
    }
}