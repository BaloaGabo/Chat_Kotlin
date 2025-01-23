package com.example.chat_kotlin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chat_kotlin.databinding.ActivityCambiarPasswordBinding

class CambiarPassword : AppCompatActivity() {

    private lateinit var binding:ActivityCambiarPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCambiarPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}