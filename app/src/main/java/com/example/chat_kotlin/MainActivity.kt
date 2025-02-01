package com.example.chat_kotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chat_kotlin.Fragmentos.FragmentChats
import com.example.chat_kotlin.Fragmentos.FragmentPerfil
import com.example.chat_kotlin.Fragmentos.FragmentUsuarios
import com.example.chat_kotlin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser == null){
            irOpcionesLogin()
        }

        //Fragmento por Defecto
        verFragmentoPerfil()

        binding.bottomNV.setOnItemSelectedListener { item->
           when(item.itemId){
               R.id.item_perfil->{
                   //Visualizar Fragmento perfil
                   verFragmentoPerfil()
                   true
               }
               R.id.item_usuarios->{
                   //Visualizar Fragmento usuarios
                   verFragmentoUsuarios()
                   true
               }
               R.id.item_chat->{
                   //Visualizar Fragmento chat
                   verFragmentoChats()
                   true
               }
               else ->{
                   false
               }
           }
        }
    }

    private fun irOpcionesLogin() {
        startActivity(Intent(applicationContext, OpcionesLoginActivity::class.java))
        finishAffinity()
    }

    private fun verFragmentoPerfil(){

        binding.tvTitulo.text = "Perfil"

        val fragment = FragmentPerfil()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFl.id,fragment,"Fragment Perfil")
        fragmentTransaction.commit()

    }
    private fun verFragmentoUsuarios(){

        binding.tvTitulo.text = "Usuarios"

        val fragment = FragmentUsuarios()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFl.id,fragment,"Fragment Usuarios")
        fragmentTransaction.commit()
    }
    private fun verFragmentoChats(){

        binding.tvTitulo.text = "Chats"

        val fragment = FragmentChats()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFl.id,fragment,"Fragment Chats")
        fragmentTransaction.commit()
    }

    private fun actualizarEstado (estado : String){
        val ref = FirebaseDatabase.getInstance().reference.child("Usuarios").child(firebaseAuth.uid!!)

        val hashMap = HashMap<String, Any>()
        hashMap["estado"] = estado
        ref!!.updateChildren(hashMap)
    }

    override fun onResume() {
        super.onResume()
        actualizarEstado("Online")
    }

    override fun onPause() {
        super.onPause()
        actualizarEstado("Offline")
    }
}