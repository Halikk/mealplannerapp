package com.example.mealplannerapp.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.mealplannerapp.data.MealPlannerDatabase
import com.example.mealplannerapp.viewmodel.MealStorageViewModel
import com.example.mealplannerapp.databinding.ActivityMealDetailBinding
import com.example.mealplannerapp.viewmodel.MealStorageViewModelFactory
import com.example.mealplannerapp.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MealDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMealDetailBinding
    private val mealStorageViewModel: MealStorageViewModel by viewModels {
        MealStorageViewModelFactory(
            MealPlannerDatabase.getInstance(applicationContext).mealDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Intent'ten gelen Meal ID'yi al
        val mealId = intent.getIntExtra("MEAL_ID", -1)
        val fromSavedMeals = intent.getBooleanExtra("FROM_SAVED_MEALS", false)
        if (fromSavedMeals) {
            binding.btnSaveMeal.visibility = View.GONE
        }

        if (mealId != -1) {
            // API'den yemek detaylarını çek
            fetchMealDetails(mealId)
        } else {
            Toast.makeText(this, "Meal details are missing.", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Kaydetme işlemi
        binding.btnSaveMeal.setOnClickListener {
            val mealTitle = binding.tvTitle.text.toString()
            val mealImage = intent.getStringExtra("MEAL_IMAGE") ?: ""
            val mealCalories = intent.getDoubleExtra("MEAL_CALORIES", 0.0)
            val mealApiId = intent.getIntExtra("MEAL_ID", -1) // mealApiId burada alınıyor
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null && mealApiId != -1) {
                showDayPickerDialog(mealTitle, mealApiId, mealImage, mealCalories, currentUser.uid)
            } else {
                Toast.makeText(this, "Meal details are missing.", Toast.LENGTH_SHORT).show()
            }
        }

        // Paylaşma işlemi
        binding.btnShareMeal.setOnClickListener {
            shareMealDetails()
        }
    }

    private fun fetchMealDetails(mealId: Int) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getMealDetails(
                    recipeId = mealId,
                    apiKey = "23669ef999af4ddba191f61b71f6f9f7"
                )
                if (response.isSuccessful && response.body() != null) {
                    val mealDetails = response.body()!!


                    displayMealDetails(
                        title = mealDetails.title,
                        image = mealDetails.image,
                        instructions = mealDetails.instructions

                    )
                } else {
                    Toast.makeText(this@MealDetailActivity, "Meal details are missing.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MealDetailActivity, "Bir hata oluştu.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDayPickerDialog(title: String, mealApiId: Int, img: String, cals: Double, userId: String) {
        val daysOfWeek = arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        AlertDialog.Builder(this)
            .setTitle("Which day?")
            .setItems(daysOfWeek) { _, which ->
                val selectedDay = daysOfWeek[which]
                saveMealToDatabase(selectedDay, title, mealApiId, img, cals, userId)
            }
            .create()
            .show()
    }

    private fun saveMealToDatabase(dayOfWeek: String, title: String, mealApiId: Int, imageUrl: String, calories: Double, userId: String) {
        lifecycleScope.launch {
            mealStorageViewModel.saveMeal(dayOfWeek, title, mealApiId, imageUrl, calories, userId)
            Toast.makeText(this@MealDetailActivity, "Saved to $dayOfWeek", Toast.LENGTH_SHORT).show()
        }
    }
    private fun shareMealDetails() {
        val mealTitle = binding.tvTitle.text.toString()
        val mealInstructions = binding.tvInstructions.text.toString()

        // Örtülü intent oluştur
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                "Check out this meal: $mealTitle\n\nInstructions:\n$mealInstructions"
            )
        }
        // Paylaşım ekranını başlat
        startActivity(Intent.createChooser(shareIntent, "Share this meal via"))
    }
    @SuppressLint("SetTextI18n")
    private fun displayMealDetails(title: String, image: String, instructions: String?) {
        binding.tvTitle.text = title
        binding.ivMeal.load(image)

        val cleanInstructions = Html.fromHtml(
            instructions ?: "No instructions available.",
            Html.FROM_HTML_MODE_COMPACT
        ).toString()
        binding.tvInstructions.text = cleanInstructions.ifBlank { "No instructions available." }

    }
}