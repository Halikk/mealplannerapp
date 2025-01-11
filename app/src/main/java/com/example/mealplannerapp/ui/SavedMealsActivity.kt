package com.example.mealplannerapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealplannerapp.data.MealEntry
import com.example.mealplannerapp.data.MealPlannerDatabase
import com.example.mealplannerapp.databinding.ActivitySavedMealsBinding
import com.example.mealplannerapp.network.RetrofitClient
import com.example.mealplannerapp.viewmodel.MealStorageViewModel
import com.example.mealplannerapp.viewmodel.MealStorageViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SavedMealsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedMealsBinding
    private val mealStorageViewModel: MealStorageViewModel by viewModels {
        MealStorageViewModelFactory(
            MealPlannerDatabase.getInstance(applicationContext).mealDao()
        )
    }
    private lateinit var adapter: SavedMealsAdapter
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedMealsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Kontrol: Kullanıcı oturum açmış mı?
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to view saved meals.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // RecyclerView setup
        setupRecyclerView()

        // Saved meals yükle
        loadMealsForCurrentDay()
    }

    private fun setupRecyclerView() {
        adapter = SavedMealsAdapter(
            onDeleteClick = { mealEntry ->
                deleteMeal(mealEntry)
            },
            onDetailClick = { mealEntry -> // mealEntry nesnesi geçiliyor
                fetchMealDetailsFromApi(mealEntry.mealApiId)
            }
        )

        binding.recyclerViewSavedMeals.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewSavedMeals.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    private fun loadMealsForCurrentDay() {
        val day = intent.getStringExtra("DAY_OF_WEEK") ?: "Monday"

        lifecycleScope.launch {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                mealStorageViewModel.fetchMealsByDayForUser(day, currentUser.uid)
                mealStorageViewModel.mealsByDay.collect { meals ->
                    if (meals.isNotEmpty()) {
                        adapter.submitList(meals)
                        binding.tvEmptyState.visibility = View.GONE
                    } else {
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.tvEmptyState.text = "No meals found for $day."
                    }
                }
            }
        }
    }

    private fun fetchMealDetailsFromApi(mealApiId: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getMealDetails(
                    recipeId = mealApiId,
                    apiKey = "23669ef999af4ddba191f61b71f6f9f7"
                )
                if (response.isSuccessful && response.body() != null) {
                    val mealDetails = response.body()!!

                    // MealEntry nesnesi oluştur
                    val mealEntry = MealEntry(
                        id = 0, // Veritabanı tarafından otomatik oluşturulur
                        dayOfWeek = "Unknown", // Gerekirse uygun bir değer ekleyin
                        title = mealDetails.title,
                        mealApiId = mealApiId,
                        imageUrl = mealDetails.image,
                        calories = 0.0, // Eğer kalori bilgisi mealDetails içinde yoksa
                        userId = FirebaseAuth.getInstance().currentUser?.uid ?: "Unknown"
                    )

                    // Yeni nesneyi navigateToMealDetail'a gönder
                    navigateToMealDetail(mealEntry)
                } else {
                    Toast.makeText(this@SavedMealsActivity, "Meal details could not be retrieved.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@SavedMealsActivity, "An error occurred while fetching meal details.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteMeal(mealEntry: MealEntry) {
        lifecycleScope.launch {
            mealStorageViewModel.deleteMeal(mealEntry.id)
            Toast.makeText(this@SavedMealsActivity, "${mealEntry.title} deleted!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToMealDetail(mealEntry: MealEntry) {
        val intent = Intent(this, MealDetailActivity::class.java).apply {
            putExtra("MEAL_ID", mealEntry.mealApiId)
            putExtra("FROM_SAVED_MEALS", true) // Saved Meals'tan gelindiğini belirt
        }
        startActivity(intent)
    }
}