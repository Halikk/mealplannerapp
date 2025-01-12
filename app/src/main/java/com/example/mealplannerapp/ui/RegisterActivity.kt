package com.example.mealplannerapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mealplannerapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    // ActivityRegisterBinding nesnesi, kullanıcı arayüzüne erişmek için kullanılır
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Kullanıcı arayüzü bağlanıyor
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // "Register" butonuna tıklanma olayını dinler
        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString() // Kullanıcının girdiği e-posta
            val password = binding.etPassword.text.toString() // Kullanıcının girdiği şifre

            // E-posta ve şifre boş değilse kayıt işlemini başlat
            if (email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(email, password)
            } else {
                // Eğer alanlar boşsa bir uyarı mesajı göster
                Toast.makeText(this, "Lütfen e-posta ve şifre giriniz.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Kullanıcıyı Firebase Authentication ile kaydeden metod
    private fun registerUser(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Kayıt başarılı olduğunda mesaj göster ve aktiviteyi sonlandır
                    Toast.makeText(this, "Kayıt başarılı!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // Kayıt başarısız olduğunda hata mesajını göster
                    Toast.makeText(
                        this,
                        "Kayıt başarısız: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}