package com.example.mealplannerapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mealplannerapp.data.Meal
import com.example.mealplannerapp.databinding.ItemMealBinding
import coil.load

class MealAdapter(
    private val onMealClick: (Meal) -> Unit
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    private val items = mutableListOf<Meal>()

    fun submitList(meals: List<Meal>) {
        items.clear()
        items.addAll(meals)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = items[position]
        holder.bind(meal)
    }

    override fun getItemCount() = items.size

    inner class MealViewHolder(private val binding: ItemMealBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(meal: Meal) {
            binding.tvMealTitle.text = meal.title
            binding.tvMealCalories.text = "Calories: ${meal.calories}"
            binding.ivMealImage.load(meal.image)
            binding.root.setOnClickListener {
                onMealClick(meal)
            }
        }
    }
}