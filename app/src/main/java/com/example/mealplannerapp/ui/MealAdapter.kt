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
    private val onMealClick: (Meal) -> Unit // Bir yemek seçildiğinde çalıştırılacak lambda fonksiyonu
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Any>() // Hem yemekler (Meal) hem de başlıkları (String) tutacak bir liste

    companion object {
        private const val VIEW_TYPE_MEAL = 1 // Yemek öğesi görünüm tipi
        private const val VIEW_TYPE_HEADER = 2 // Başlık görünüm tipi
    }

    // Yemek listesini başlıklarla birlikte düzenler ve RecyclerView'e ekler
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(meals: List<Meal>) {
        items.clear() // Listeyi temizle
        if (meals.isNotEmpty()) {
            items.add("Breakfast") // Kahvaltı başlığı ekle
            items.addAll(meals.take(1)) // İlk yemek kahvaltı

            if (meals.size > 1) {
                items.add("Lunch") // Öğle yemeği başlığı ekle
                items.addAll(meals.subList(1, 2)) // İkinci yemek öğle yemeği
            }

            if (meals.size > 2) {
                items.add("Dinner") // Akşam yemeği başlığı ekle
                items.addAll(meals.subList(2, meals.size)) // Kalan yemekler akşam yemeği
            }
        }
        notifyDataSetChanged() // Listeyi güncelle
    }

    // Öğenin tipine göre görünüm tipi döndürülür (Header veya Meal)
    override fun getItemViewType(position: Int): Int {
        return if (items[position] is String) {
            VIEW_TYPE_HEADER // Başlık tipi
        } else {
            VIEW_TYPE_MEAL // Yemek tipi
        }
    }

    // Görünüm tipi Header veya Meal olduğuna göre ViewHolder oluştur
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val binding = ItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            HeaderViewHolder(binding) // Başlık için ViewHolder
        } else {
            val binding = ItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MealViewHolder(binding) // Yemek için ViewHolder
        }
    }

    // Görünümün bağlanma işlemi (Header veya Meal)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(items[position] as String) // Başlık bağlama
        } else if (holder is MealViewHolder) {
            holder.bind(items[position] as Meal) // Yemek bağlama
        }
    }

    // Listedeki öğe sayısını döndürür
    override fun getItemCount() = items.size

    // Yemekler için ViewHolder sınıfı
    inner class MealViewHolder(private val binding: ItemMealBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(meal: Meal) {
            binding.tvMealTitle.text = meal.title // Yemek başlığını ata
            binding.tvMealCalories.text = "Calories: ${meal.calories}" // Kalori bilgisini ata
            binding.ivMealImage.load(meal.image) // Yemek görselini yükle
            binding.root.setOnClickListener {
                onMealClick(meal) // Yemek tıklandığında lambda fonksiyonunu çalıştır
            }
        }
    }

    // Başlıklar için ViewHolder sınıfı
    inner class HeaderViewHolder(private val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(header: String) {
            binding.tvHeader.text = header // Başlık metnini ata
        }
    }
}