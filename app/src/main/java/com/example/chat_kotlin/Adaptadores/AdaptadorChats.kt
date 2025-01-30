package com.example.chat_kotlin.Adaptadores

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chat_kotlin.Chat.ChatActivity
import com.example.chat_kotlin.Constantes
import com.example.chat_kotlin.Modelos.Chats
import com.example.chat_kotlin.R
import com.example.chat_kotlin.databinding.ItemChatsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdaptadorChats : RecyclerView.Adapter<AdaptadorChats.HolderChats> {

    private var context : Context
    private var chatArrayList : ArrayList<Chats>
    private lateinit var binding : ItemChatsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var miUid = ""

    constructor(context: Context, chatArrayList: ArrayList<Chats>) {
        this.context = context
        this.chatArrayList = chatArrayList
        firebaseAuth = FirebaseAuth.getInstance()
        miUid = firebaseAuth.uid!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderChats {
    binding = ItemChatsBinding.inflate(LayoutInflater.from(context),parent, false)
        return HolderChats(binding.root)
    }

    override fun getItemCount(): Int {
        return chatArrayList.size
    }

    override fun onBindViewHolder(holder: HolderChats, position: Int) {
        val modeloChats = chatArrayList[position]

        cargarUltimoMensaje(modeloChats, holder)

        holder.itemView.setOnClickListener{
            val uidRecibimos = modeloChats.uidRecibimos
            if (uidRecibimos!=null){
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("uid",uidRecibimos)
                context.startActivity(intent)
            }
        }
    }

    private fun cargarUltimoMensaje(modeloChats: Chats, holder: AdaptadorChats.HolderChats) {

        val chatKey = modeloChats.keyChat

        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.child(chatKey).limitToLast(1)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children){
                        val emisorUid = "${ds.child("emisorUid").value}"
                        val idMensaje = "${ds.child("idMensaje").value}"
                        val mensaje = "${ds.child("mensaje").value}"
                        val receptorUid = "${ds.child("receptorUid").value}"
                        val tiempo = ds.child("tiempo").value as Long
                        val tipoMensaje = "${ds.child("tipoMensaje").value}"

                        val formatoFechaHora = Constantes.obtenerFechaHora(tiempo)

                        modeloChats.emisorUid = emisorUid
                        modeloChats.idMensaje = idMensaje
                        modeloChats.mensaje = mensaje
                        modeloChats.receptorUid = receptorUid
                        modeloChats.tipoMensaje = tipoMensaje

                        holder.tvFecha.text = "$formatoFechaHora"

                        if (tipoMensaje == Constantes.MENSAJE_TIPO_TEXTO){
                            holder.tvUltimoMensaje.text = mensaje

                        }else{
                            holder.tvUltimoMensaje.text = "Se ha enviado una imagen"
                        }

                        cargarInfoUsuarioRecibido(modeloChats, holder)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

    }

    private fun cargarInfoUsuarioRecibido(modeloChats: Chats, holder: AdaptadorChats.HolderChats) {

        val emisorUid = modeloChats.emisorUid
        val receptorUid = modeloChats.receptorUid

        var uidRecibimos = ""
        if (emisorUid == miUid){
            uidRecibimos = receptorUid
        }else{
            uidRecibimos = emisorUid
        }

        modeloChats.uidRecibimos = uidRecibimos

        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child(uidRecibimos)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nombres = "${snapshot.child("nombres").value}"
                    val imagen = "${snapshot.child("imagen").value}"

                    modeloChats.nombres = nombres
                    modeloChats.imagen = imagen

                    holder.tvNombres.text = nombres

                    try {

                        Glide.with(context)
                            .load(imagen)
                            .placeholder(R.drawable.ic_img_perfil)
                            .into(holder.IvPerfil)

                    }catch (e:Exception){

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    inner class HolderChats (itemView : View) : RecyclerView.ViewHolder(itemView){
        var IvPerfil = binding.IvPerfil
        var tvNombres = binding.tvNombres
        var tvUltimoMensaje = binding.tvUltimoMensaje
        var tvFecha = binding.tvFecha
    }

}