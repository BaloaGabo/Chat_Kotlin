package com.example.chat_kotlin.Fragmentos

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chat_kotlin.Adaptadores.AdaptadorUsuario
import com.example.chat_kotlin.Modelos.Usuario
import com.example.chat_kotlin.databinding.FragmentUsuariosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FragmentUsuarios : Fragment() {

    private lateinit var binding : FragmentUsuariosBinding


    private lateinit var mContext : Context
    private var usuarioAdaptador : AdaptadorUsuario?=null
    private var usuarioLista : List<Usuario>?=null

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUsuariosBinding.inflate(layoutInflater, container, false)

        binding.RVUsuarios.setHasFixedSize(true)
        binding.RVUsuarios.layoutManager = LinearLayoutManager(mContext)

        usuarioLista = ArrayList()

        binding.etBuscarUsuario.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(usuario: CharSequence?, p1: Int, p2: Int, p3: Int) {
                buscarUsuario(usuario.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        listaUsuarios()

        return binding.root
    }

    private fun listaUsuarios() {

        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        val reference = FirebaseDatabase.getInstance().reference.child("Usuarios").orderByChild("nombres")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (usuarioLista as ArrayList<Usuario>).clear()
                if (binding.etBuscarUsuario.text.toString().isEmpty()){

                    for (sn in snapshot.children){
                        val usuario : Usuario? = sn.getValue(Usuario::class.java)
                        if(!(usuario!!.uid).equals(firebaseUser)){
                            (usuarioLista as ArrayList<Usuario>).add(usuario)
                        }
                    }
                }
                usuarioAdaptador = AdaptadorUsuario(mContext, usuarioLista!!)
                binding.RVUsuarios.adapter = usuarioAdaptador
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun buscarUsuario (usuario: String){
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!.uid
        val reference = FirebaseDatabase.getInstance().reference.child("Usuarios").orderByChild("nombres")
            .startAt(usuario).endAt(usuario+"\uf8ff")
        reference.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (usuarioLista as ArrayList<Usuario>).clear()
                for (ss in snapshot.children){
                    val usuario : Usuario?= ss.getValue(Usuario::class.java)
                    if(!(usuario!!.uid).equals(firebaseUser)){
                        (usuarioLista as ArrayList<Usuario>).add(usuario)

                    }
                }
                usuarioAdaptador = AdaptadorUsuario(mContext, usuarioLista!!)
                binding.RVUsuarios.adapter = usuarioAdaptador
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}