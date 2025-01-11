package com.example.mealplannerapp.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mealplannerapp.data.MealEntry
import com.example.mealplannerapp.databinding.ItemSavedMealBinding

class SavedMealsAdapter(
    private val onDeleteClick: (MealEntry) -> Unit,
    private val onDetailClick: (MealEntry) -> Unit // Detay için yeni lambda
) : ListAdapter<MealEntry, SavedMealsAdapter.MealViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemSavedMealBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = getItem(position)
        holder.bind(meal, onDeleteClick, onDetailClick)
    }

    class MealViewHolder(private val binding: ItemSavedMealBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(
            meal: MealEntry,
            onDeleteClick: (MealEntry) -> Unit,
            onDetailClick: (MealEntry) -> Unit
        ) {
            binding.tvMealTitle.text = meal.title
            binding.ivMealImage.load(meal.imageUrl)
            binding.tvMealCalories.text = "Calories: ${meal.calories}"

            // Silme işlemi
            binding.btnDeleteMeal.setOnClickListener {
                onDeleteClick(meal)
            }

            // Detaylara gitmek için tıklama
            binding.root.setOnClickListener {
                onDetailClick(meal) // Sadece ID'yi gönderiyoruz
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MealEntry>() {
            override fun areItemsTheSame(oldItem: MealEntry, newItem: MealEntry): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MealEntry, newItem: MealEntry): Boolean {
                return oldItem == newItem
            }
        }
    }
}