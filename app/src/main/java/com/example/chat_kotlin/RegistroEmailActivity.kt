package com.example.chat_kotlin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chat_kotlin.databinding.ActivityRegistroEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnRegistrar.setOnClickListener{
            validarInformacion()
        }
    }

    private var nombres = ""
    private var email = ""
    private var password = ""
    private var r_password = ""

    private fun validarInformacion() {
        nombres = binding.etNombres.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        r_password = binding.etRPassword.text.toString().trim()

        if (nombres.isEmpty()){
            binding.etNombres.error = "Ingrese Nombre"
            binding.etNombres.requestFocus()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error = "Correo es invalido"
            binding.etEmail.requestFocus()
        }
        else if (email.isEmpty()){
            binding.etEmail.error = "Ingrese Correo"
            binding.etEmail.requestFocus()
        }
        else if (password.isEmpty()){
            binding.etPassword.error = "Ingrese Contraseña"
            binding.etPassword.requestFocus()
        }
        else if (r_password.isEmpty()){
            binding.etRPassword.error = "Repita Contraseña"
            binding.etRPassword.requestFocus()

        }
        else if (password != r_password){
            binding.etRPassword.error = "No coinciden las Contraseñas"
            binding.etRPassword.requestFocus()
        }
        else{
            registrarUsuario()
        }
    }

    private fun registrarUsuario() {
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                actualizarInformacion()

            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Fallo la creacion de la cuenta debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

            }
    }

    private fun actualizarInformacion() {
        progressDialog.setMessage("Guardando Informacion")

        val uidU = firebaseAuth.uid
        val nombresU = nombres
        val emailU = firebaseAuth.currentUser!!.email
        val tiempoR = Constantes.obtenerTiempoD()

        val datosUsuario = HashMap<String, Any>()

        datosUsuario["uid"] = "$uidU"
        datosUsuario["nombres"] = "$nombresU"
        datosUsuario["email"] = "$emailU"
        datosUsuario["tiempoR"] = "$tiempoR"
        datosUsuario["proveedor"] = "Email"
        datosUsuario["estado"] = "Online"

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uidU!!)
            .setValue(datosUsuario)
            .addOnSuccessListener {
                progressDialog.dismiss()

                startActivity(Intent(applicationContext, MainActivity::class.java))
                finishAffinity()

            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Fallo la creacion de la cuenta debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }



    }
}