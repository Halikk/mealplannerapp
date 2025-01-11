package com.example.mealplannerapp.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mealplannerapp.data.Meal
import com.example.mealplannerapp.databinding.ItemHeaderBinding
import com.example.mealplannerapp.databinding.ItemMealBinding

class MealAdapter(
    private val onMealClick: (Meal) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Any>() // Hem Meal hem de Header String'leri tutacak

    companion object {
        private const val VIEW_TYPE_MEAL = 1
        private const val VIEW_TYPE_HEADER = 2
    }

    // Listeyi düzenleyerek başlık ekler
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(meals: List<Meal>) {
        items.clear()
        if (meals.isNotEmpty()) {
            items.add("Breakfast")
            items.addAll(meals.take(1)) // İlk yemek kahvaltı

            if (meals.size > 1) {
                items.add("Lunch")
                items.addAll(meals.subList(1, 2)) // İkinci yemek öğle yemeği
            }

            if (meals.size > 2) {
                items.add("Dinner")
                items.addAll(meals.subList(2, meals.size)) // Kalan yemekler akşam yemeği
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is String) {
            VIEW_TYPE_HEADER
        } else {
            VIEW_TYPE_MEAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(binding)
        } else {
            val binding = ItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MealViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(items[position] as String)
        } else if (holder is MealViewHolder) {
            holder.bind(items[position] as Meal)
        }
    }

    override fun getItemCount() = items.size

    // ViewHolder for Meal items
    inner class MealViewHolder(private val binding: ItemMealBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(meal: Meal) {
            binding.tvMealTitle.text = meal.title
            binding.tvMealCalories.text = "Calories: ${meal.calories}"
            binding.ivMealImage.load(meal.image)
            binding.root.setOnClickListener {
                onMealClick(meal)
            }
        }
    }

    // ViewHolder for Headers
    inner class HeaderViewHolder(private val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(header: String) {
            binding.tvHeader.text = header
        }
    }
}