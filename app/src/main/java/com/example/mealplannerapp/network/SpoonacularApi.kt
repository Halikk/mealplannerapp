package com.example.mealplannerapp.network

import com.example.mealplannerapp.data.RecipeDetails
import com.example.mealplannerapp.data.SpoonacularResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularApi {

    @GET("recipes/complexSearch")
    suspend fun getMealsByType(
        @Query("apiKey") apiKey: String,
        @Query("type") type: String,                    // "breakfast", "lunch", "main course", vb.
        @Query("minCalories") minCalories: Int = 0,     // opsiyonel
        @Query("maxCalories") maxCalories: Int = 1000,  // opsiyonel
        @Query("number") number: Int = 10,
        @Query("includeNutrition") includeNutrition: Boolean = true
    ): Response<SpoonacularResponse>
    @GET("recipes/{id}/information")
    suspend fun getMealDetails(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String
    ): Response<RecipeDetails>


}