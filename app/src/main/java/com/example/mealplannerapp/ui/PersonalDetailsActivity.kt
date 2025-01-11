package com.example.mealplannerapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mealplannerapp.databinding.ActivityPersonalDetailsBinding

class PersonalDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonalDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalculate.setOnClickListener {
            val ageText = binding.etAge.text.toString()
            val heightText = binding.etHeight.text.toString()
            val weightText = binding.etWeight.text.toString()
            val selectedGender = if (binding.radioMale.isChecked) "Male" else "Female"

            // Değerleri parse et
            val age = ageText.toIntOrNull() ?: 25
            val height = heightText.toIntOrNull() ?: 170
            val weight = weightText.toDoubleOrNull() ?: 70.0

            // Intent ile MealPlanActivity'ye gönder
            val intent = Intent(this, MealPlanActivity::class.java)
            intent.putExtra("AGE", age)
            intent.putExtra("HEIGHT", height)
            intent.putExtra("WEIGHT", weight)
            intent.putExtra("GENDER", selectedGender)
            startActivity(intent)
        }
    }
}