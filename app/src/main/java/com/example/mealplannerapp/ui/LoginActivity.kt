package com.example.mealplannerapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mealplannerapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
        // Register butonuna tıklandığında RegisterActivity'ye geçiş
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                    // Kullanıcı kalori hesaplama yapmış mı kontrol et
                    val sharedPreferences = getSharedPreferences("MealPlannerPrefs", MODE_PRIVATE)
                    val hasCalculatedCalories = sharedPreferences.getBoolean("HAS_CALCULATED_CALORIES", false)

                    // Kullanıcı kalori hesapladıysa doğrudan MealPlanActivity'ye yönlendir, yoksa CalorieCalculationActivity'ye
                    val intent = if (hasCalculatedCalories) {
                        Intent(this, MealPlanActivity::class.java)
                    } else {
                        Intent(this, PersonalDetailsActivity::class.java)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}