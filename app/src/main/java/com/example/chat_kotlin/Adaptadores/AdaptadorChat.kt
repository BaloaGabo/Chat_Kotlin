package com.example.chat_kotlin.Adaptadores

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chat_kotlin.Constantes
import com.example.chat_kotlin.Modelos.Chat
import com.example.chat_kotlin.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth

class AdaptadorChat : RecyclerView.Adapter<AdaptadorChat.HolderChat> {
    private val context : Context
    private val chatArray : ArrayList<Chat>
    private val firebaseAuth : FirebaseAuth

    companion object {
        private const val MENSAJE_IZQUIERDO = 0
        private const val MENSAJE_DERECHO = 1
    }

    constructor(context: Context, chatArray: ArrayList<Chat>) {
        this.context = context
        this.chatArray = chatArray
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderChat {
        if (viewType == MENSAJE_DERECHO){
            val view = LayoutInflater.from(context).inflate(R.layout.item_chat_derecho,parent,false)
            return HolderChat(view)
        }else{
            val view = LayoutInflater.from(context).inflate(R.layout.item_chat_izquierdo,parent,false)
            return HolderChat(view)
        }
    }

    override fun getItemCount(): Int {
        return chatArray.size
    }

    override fun onBindViewHolder(holder: HolderChat, position: Int) {
        val modeloChat = chatArray[position]

        val mensaje = modeloChat.mensaje
        val tipoMensaje = modeloChat.tipoMensaje
        val tiempo = modeloChat.tiempo

        val formato_fecha_hora = Constantes.obtenerFechaHora(tiempo)
        holder.Tv_timepo_mensaje.text = formato_fecha_hora

        if (tipoMensaje == Constantes.MENSAJE_TIPO_TEXTO){
            holder.Tv_mensaje.visibility = View.VISIBLE
            holder.Iv_mensaje.visibility = View.GONE

            holder.Tv_mensaje.text = mensaje
        }else{
            holder.Tv_mensaje.visibility = View.GONE
            holder.Iv_mensaje.visibility = View.VISIBLE

            try {
                Glide.with(context)
                    .load(mensaje)
                    .placeholder(R.drawable.imagen_enviada)
                    .error(R.drawable.imagen_chat_falla)
                    .into(holder.Iv_mensaje)

            }catch (e:Exception){

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (chatArray[position].emisorUid == firebaseAuth.uid){
            return MENSAJE_DERECHO
        }else{
            return MENSAJE_IZQUIERDO
        }
    }

    inner class HolderChat(itemView : View) : RecyclerView.ViewHolder(itemView){
        var Tv_mensaje : TextView = itemView.findViewById(R.id.Tv_mensaje)
        var Iv_mensaje : ShapeableImageView = itemView.findViewById(R.id.Iv_mensaje)
        var Tv_timepo_mensaje : TextView = itemView.findViewById(R.id.Tv_tiempo_mensaje)
    }

}