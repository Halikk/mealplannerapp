package com.example.mealplannerapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealplannerapp.databinding.ActivityDaysBinding

class DaysActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDaysBinding
    private val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDaysBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = DaysAdapter(daysOfWeek) { selectedDay ->
            // Seçilen güne tıklandığında SavedMealsActivity'ye geçiş yap
            val intent = Intent(this, SavedMealsActivity::class.java)
            intent.putExtra("DAY_OF_WEEK", selectedDay)
            startActivity(intent)
        }

        binding.recyclerViewDays.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewDays.adapter = adapter
    }
}