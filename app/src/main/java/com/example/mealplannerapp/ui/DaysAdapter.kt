package com.example.mealplannerapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mealplannerapp.databinding.ItemDayBinding

class DaysAdapter(
    private val days: List<String>, // Haftanın günlerini içeren bir liste
    private val onDayClick: (String) -> Unit // Gün seçildiğinde çalıştırılacak lambda fonksiyonu
) : RecyclerView.Adapter<DaysAdapter.DayViewHolder>() {

    // ViewHolder oluşturma işlemi
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        // Görünüm bağlama işlemi
        val binding = ItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    // ViewHolder'a veri bağlama işlemi
    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position] // Listedeki belirli bir günü al
        holder.bind(day) // Günü ViewHolder'a bağla
    }

    // Listedeki öğe sayısını döndürür
    override fun getItemCount(): Int = days.size

    // ViewHolder sınıfı, her bir gün için görünümü yönetir
    inner class DayViewHolder(private val binding: ItemDayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(day: String) {
            binding.tvDayName.text = day // Gün adını TextView'e ata
            binding.root.setOnClickListener {
                onDayClick(day) // Gün seçildiğinde lambda fonksiyonunu çalıştır
            }
        }
    }
}