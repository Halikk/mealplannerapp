package com.example.mealplannerapp.data

import com.google.gson.annotations.SerializedName

data class SpoonacularResponse(
    @SerializedName("results")
    val results: List<Recipe>? = null,

    // If using the random endpoint:
    @SerializedName("recipes")
    val randomRecipes: List<Recipe>? = null
)

data class Recipe(
    val id: Int?,
    val title: String?,
    val image: String?,
    val nutrition: NutritionInfo?
)

data class NutritionInfo(
    @SerializedName("nutrients")
    val nutrients: List<Nutrient>?
)

data class Nutrient(
    val name: String?,
    val amount: Double?,
    val unit: String?
)

data class RecipeDetails(
    val id: Int,
    val title: String,
    val image: String,
    val instructions: String
)