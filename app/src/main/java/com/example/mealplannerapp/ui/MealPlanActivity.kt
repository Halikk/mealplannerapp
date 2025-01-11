package com.example.mealplannerapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealplannerapp.databinding.ActivityMealPlanBinding
import com.example.mealplannerapp.viewmodel.MealViewModel
import com.google.firebase.auth.FirebaseAuth

@Suppress("DEPRECATION")
class MealPlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMealPlanBinding

    // Kullanıcıdan aldığımız yaş, boy, kilo, cinsiyet ile kalori hesabını yöneten ViewModel
    private val mealViewModel: MealViewModel by viewModels()

    // RecyclerView adaptörü
    private lateinit var mealAdapter: MealAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Kullanıcı giriş yapmamışsa LoginActivity'ye yönlendirme
        if (FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // 1) Intent'ten kullanıcı verilerini al
        val age = intent.getIntExtra("AGE", 25)
        val height = intent.getIntExtra("HEIGHT", 170)
        val weight = intent.getDoubleExtra("WEIGHT", 70.0)
        val gender = intent.getStringExtra("GENDER") ?: "Male"

        // 2) Bu verileri MealViewModel'e aktar
        mealViewModel.age = age
        mealViewModel.height = height
        mealViewModel.weight = weight
        mealViewModel.gender = gender

        // 3) Günlük kalori hesapla
        mealViewModel.calculateDailyCalories()

        // 4) Kalori bilgisini dinle, ekranda göster
        lifecycleScope.launchWhenStarted {
            mealViewModel.dailyCaloriesFlow.collect { cals ->
                binding.tvDailyCals.text = "Daily Calories: $cals"
            }
        }

        // 5) RecyclerView ve Adapter
        mealAdapter = MealAdapter { meal ->
            val intent = Intent(this, MealDetailActivity::class.java).apply {
                putExtra("MEAL_ID", meal.id) // Yemeğin ID'sini gönder
                putExtra("MEAL_TITLE", meal.title)
                putExtra("MEAL_IMAGE", meal.image)
                putExtra("MEAL_CALORIES", meal.calories)
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = mealAdapter

        // 6) mealsFlow’u dinle -> listeye yansıt
        lifecycleScope.launchWhenStarted {
            mealViewModel.mealsFlow.collect { meals ->
                mealAdapter.submitList(meals)
            }
        }

        // 7) “Fetch Meals” butonuna basınca 3 öğün çek
        binding.btnFetchMeals.setOnClickListener {
            mealViewModel.fetchThreeMealsSeparately()
        }

        // 8) Kaydedilen yemekler butonu
        binding.btnViewSavedMeals.setOnClickListener {
            val intent = Intent(this, DaysActivity::class.java)
            startActivity(intent)
        }
    }
}