package com.madcryk.findevent.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.madcryk.findevent.R
import com.madcryk.findevent.activities.ChatActivity
import com.madcryk.findevent.models.Message
import com.makeramen.roundedimageview.RoundedImageView
import java.text.DateFormat
import java.text.DateFormat.getDateTimeInstance
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessagesAdapter( _view : ChatActivity ) : RecyclerView.Adapter<MessagesViewHolder>() {
    val view = _view
    private var messages : ArrayList<Message> = ArrayList()
    private var storage = FirebaseStorage.getInstance()
    private var userUid = view.getUserUid()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MessagesViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false))

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        holder.bind(messages[position], storage, userUid)
    }

    fun addMessage(message: Message){
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}

class MessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val userIcon = itemView.findViewById<RoundedImageView>(R.id.userIconMessage)
    private val textMessage = itemView.findViewById<TextView>(R.id.userTextMessage)
    private val userNameMessage = itemView.findViewById<TextView>(R.id.userNameMessage)
    private val timePostedMessage = itemView.findViewById<TextView>(R.id.timePostedMessage)
    private val backgroundItemMessage = itemView.findViewById<LinearLayout>(R.id.backgroundItemMessage)

    fun bind(
        message: Message,
        storage: FirebaseStorage,
        userUid: String
    ) {
        val load = storage.getReference("icon_image_users").child(message.userUid + "icon_image")
        load.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(itemView)
                .load(uri)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(userIcon)
        }.addOnFailureListener {
            Log.w("storage", it.message.toString())
        }
        textMessage.text = message.text
        userNameMessage.text = message.username
        timePostedMessage.text = getTimePosted(message.timeCreated)

        if(message.userUid == userUid){
            backgroundItemMessage.setBackgroundResource(R.drawable.background_my_message)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTimePosted(timestamp: Long): CharSequence? {
        return try {
            val sdf = SimpleDateFormat("hh:mm")
            val netDate = Date(timestamp)
            sdf.format(netDate)
        } catch (e: Exception) {
            ""
        }
    }

}
