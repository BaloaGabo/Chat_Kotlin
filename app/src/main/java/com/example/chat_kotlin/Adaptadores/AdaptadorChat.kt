package com.example.chat_kotlin.Adaptadores

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chat_kotlin.Constantes
import com.example.chat_kotlin.Modelos.Chat
import com.example.chat_kotlin.R
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdaptadorChat : RecyclerView.Adapter<AdaptadorChat.HolderChat> {
    private val context : Context
    private val chatArray : ArrayList<Chat>
    private val firebaseAuth : FirebaseAuth
    private var chatRuta = ""

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

        /*Mensajes de tipo texto*/

        if (tipoMensaje == Constantes.MENSAJE_TIPO_TEXTO){
            holder.Tv_mensaje.visibility = View.VISIBLE
            holder.Iv_mensaje.visibility = View.GONE

            holder.Tv_mensaje.text = mensaje

            if(modeloChat.emisorUid.equals(firebaseAuth.uid)){
                holder.itemView.setOnClickListener{
                    val opciones = arrayOf<CharSequence>("Eliminar mensaje", "Cancelar")
                    val builder : AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("¿Que desea realizar?")
                    builder.setItems(opciones, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0){
                            eliminarMensaje(position, holder, modeloChat)
                        }
                    })
                    builder.show()
                }
            }
        }

        /*Mensajes de tipo imagen*/
        else{
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

            if (modeloChat.emisorUid.equals(firebaseAuth.uid)){
                holder.itemView.setOnClickListener {
                    val opciones = arrayOf<CharSequence>("Eliminar imagen","Ver imagen", "Cancelar")
                    val builder : AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("¿Que desea realizar?")
                    builder.setItems(opciones, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0){
                            eliminarMensaje(position, holder, modeloChat)
                        }else if(which == 1){
                            visualizadorImagen(modeloChat.mensaje)
                        }
                    })
                    builder.show()

                }
            }

            else if (!modeloChat.emisorUid.equals(firebaseAuth.uid)){
                holder.itemView.setOnClickListener {
                    val opciones = arrayOf<CharSequence>("Ver imagen", "Cancelar")
                    val builder : AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("¿Que desea realizar?")
                    builder.setItems(opciones, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0){
                            visualizadorImagen(modeloChat.mensaje)
                        }

                    })
                    builder.show()

                }
            }
        }
    }

    private fun eliminarMensaje(position: Int, holder: AdaptadorChat.HolderChat, modeloChat: Chat) {

        chatRuta = Constantes.rutaChat(modeloChat.receptorUid, modeloChat.emisorUid)

        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.child(chatRuta).child(chatArray.get(position).idMensaje)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(
                    holder.itemView.context,
                    "Se ha eliminado el mensaje",
                    Toast.LENGTH_SHORT
                ).show()

            }
            .addOnFailureListener {e ->
                Toast.makeText(
                    holder.itemView.context,
                    "No se ha eliminado el mensaje debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun visualizadorImagen(imagen : String) {
        val Pv : PhotoView
        val btnCerrar : MaterialButton

        val dialog = Dialog(context)

        dialog.setContentView(R.layout.cuadro_d_visualizador_img)

        Pv = dialog.findViewById(R.id.PV_img)
        btnCerrar = dialog.findViewById(R.id.BtnCerrarVisualizador)

        try {
            Glide.with(context)
                .load(imagen)
                .placeholder(R.drawable.imagen_enviada)
                .into(Pv)

        }catch(e:Exception){

        }

        btnCerrar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
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