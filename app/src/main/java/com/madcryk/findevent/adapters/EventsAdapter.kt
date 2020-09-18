package com.madcryk.findevent.adapters

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.metalab.asyncawait.async
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.madcryk.findevent.R
import com.madcryk.findevent.impl.ItemListener
import com.madcryk.findevent.models.EventModel
import com.makeramen.roundedimageview.RoundedImageView

class EventsAdapter(_listener : ItemListener, _listEvents: ArrayList<EventModel> ) : RecyclerView.Adapter<EventsViewHolder>() {
    private var listEvents  = _listEvents
    private var eventListener : ItemListener  = _listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        EventsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_event, parent, false))

    override fun getItemCount() = listEvents.size

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        holder.bind(listEvents[position], eventListener)
    }

    fun refreshAllEvents(_listEvents : ArrayList<EventModel>){
        listEvents = _listEvents
        notifyDataSetChanged()
    }
    fun refreshItemEvent(event: EventModel, position: Int){
        listEvents[position] = event
        notifyItemChanged(position)
    }

    fun clearAllEvents() {
        listEvents.clear()
        notifyDataSetChanged()
    }

    fun addNewEvents( events:ArrayList<EventModel> ) {
        listEvents.addAll( events )
        notifyDataSetChanged()
    }
}

class EventsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val userText = itemView.findViewById<TextView>(R.id.nameItemEvent)
    private val addressText = itemView.findViewById<TextView>(R.id.addressEventItem)
    private val userImage = itemView.findViewById<RoundedImageView>(R.id.imageItemEvent)
    private val timeText = itemView.findViewById<TextView>(R.id.timeCreateItemEvent)
    private val timeStartText = itemView.findViewById<TextView>(R.id.timeStartItemEvent)
    private val userPhoneText = itemView.findViewById<TextView>(R.id.phoneItemEvent)
    private val titleText = itemView.findViewById<TextView>(R.id.titleItemEvent)
    private val descriptionText = itemView.findViewById<TextView>(R.id.descriptionItemEvent)
    private val enableCountText = itemView.findViewById<TextView>(R.id.enablePersonsItemEvent)
    private val clickEventLayout = itemView.findViewById<LinearLayout>(R.id.layoutItemEvent)
    private val isPhoneLayout = itemView.findViewById<LinearLayout>(R.id.isPhoneItemEvent)
    private var storage = FirebaseStorage.getInstance()

    fun bind(
        event: EventModel,
        eventListener: ItemListener
    ) {
        userImage.isDrawingCacheEnabled = true
        userText.text = event.userName
        addressText.text = event.address
        async {
            loadUserImage(event.userUuid, itemView)
        }
        timeStartText.text = event.time
        timeText.text = event.timeCreate
        titleText.text = event.title
        descriptionText.text = event.body
        if(event.maxPersons -  event.count <= 0){
            enableCountText.text = Resources.getSystem().getString(R.string.fill_event)
        }else
            enableCountText.text = (event.maxPersons -  event.count).toString()
        if(event.isPhone){
            userPhoneText.text = event.userPhone
            isPhoneLayout.visibility = View.VISIBLE
        }
        clickEventLayout.setOnClickListener {
            val bitmap = userImage.drawingCache
            eventListener.onClickItem(event, bitmap)

        }
    }

    private fun loadUserImage(userUuid: String, itemView: View) {
        val load = storage.getReference("icon_image_users").child(userUuid + "icon_image")
        load.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(itemView)
                .load(uri)
                .into(itemView.findViewById<RoundedImageView>(R.id.imageItemEvent))
        }.addOnFailureListener {
            Log.w("storage", it.message.toString())
        }
    }

}
