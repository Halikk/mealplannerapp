// Theme.kt
package com.example.mealplannerapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable

@Composable
fun MealPlannerAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(

        content = content
    )
}