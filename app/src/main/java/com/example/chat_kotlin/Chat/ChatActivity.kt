package com.example.chat_kotlin.Chat

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chat_kotlin.R
import com.example.chat_kotlin.databinding.ActivityChatBinding
import com.example.chat_kotlin.databinding.FragmentUsuariosBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatBinding
    private var uid = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uid = intent.getStringExtra("uid")!!

    }
}