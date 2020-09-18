package com.madcryk.findevent.adapters

import android.content.Context
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
import com.madcryk.findevent.constants.PImage.*
import com.madcryk.findevent.models.PosterModel
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.item_poster.view.*
import kotlinx.android.synthetic.main.item_poster.view.imageLeftItemPoster
import kotlinx.android.synthetic.main.item_poster.view.imageRightItemPoster

class PostersAdapter(
    _context: Context,
    _listPosters: ArrayList<PosterModel>
) : RecyclerView.Adapter<PosterViewHolder>()  {
    private var listPosters : ArrayList<PosterModel> = _listPosters
    private var context = _context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PosterViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.item_poster, parent, false)
    )

    override fun getItemCount() = listPosters.size

    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) {
        holder.bind(listPosters[position], context)
    }


    fun refreshAllPosters(_listPosters : ArrayList<PosterModel>){
        listPosters = _listPosters
        notifyDataSetChanged()
    }
    fun refreshItemPoster(poster: PosterModel, position: Int){
        listPosters[position] = poster
        notifyItemChanged(position)
    }

    fun removeAllPosters() {
        listPosters = ArrayList()
        notifyDataSetChanged()
    }

    fun addNewPosters(posters: ArrayList<PosterModel>) {
        listPosters.addAll(posters)
        notifyDataSetChanged()
    }
}

class PosterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val userText = itemView.findViewById<TextView>(R.id.nameItemPoster)
    private val userPhoneText = itemView.findViewById<TextView>(R.id.phoneItemPoster)
    private val addressText = itemView.findViewById<TextView>(R.id.addressItemPoster)
    private val timeText = itemView.findViewById<TextView>(R.id.timeStartItemPoster)
    private val titleText = itemView.findViewById<TextView>(R.id.titleItemPoster)
    private val descriptionText = itemView.findViewById<TextView>(R.id.descriptionItemPoster)
    private val isPhoneLayout = itemView.findViewById<LinearLayout>(R.id.isPhoneItemPoster)
    private val imagesLayout = itemView.findViewById<LinearLayout>(R.id.imagesLayoutItemPoster)

    // картинки для одного объявления
    private val posterImageLeft = itemView.findViewById<RoundedImageView>(R.id.imageLeftItemPoster)
    private val posterImageCenter = itemView.findViewById<RoundedImageView>(R.id.imageCenterItemPoster)
    private val posterImageRight = itemView.findViewById<RoundedImageView>(R.id.imageRightItemPoster)

    private var storage = FirebaseStorage.getInstance()

    fun bind(poster: PosterModel, context: Context) {
        userText.text = poster.userName
        if(poster.isPhone){
            userPhoneText.text = poster.userPhone
            isPhoneLayout.visibility = View.VISIBLE
        }
        addressText.text = poster.address
        async {
            loadUserImage(poster.userUid, itemView)
        }
        timeText.text = poster.dateStart
        titleText.text = poster.title
        descriptionText.text = poster.description

        val radius = (context.resources.displayMetrics.density.toInt() * 15).toFloat()
        if(poster.imageLeft.isNotEmpty() || poster.imageCenter.isNotEmpty() || poster.imageRight.isNotEmpty()){
            imagesLayout.visibility = View.VISIBLE
        }
        posterImageLeft.visibility = View.VISIBLE
        posterImageLeft.setCornerRadius(radius, 0F, radius, 0F)
        posterImageCenter.visibility = View.VISIBLE
        posterImageCenter.cornerRadius = 0F
        posterImageRight.visibility = View.VISIBLE
        posterImageRight.setCornerRadius(0F, radius, 0F, radius)
        async {
            if(poster.imageLeft.isNotEmpty() && poster.imageCenter.isNotEmpty() && poster.imageRight.isNotEmpty()){
                loadPosterImage( poster.uuid, poster.imageLeft, itemView )
                loadPosterImage( poster.uuid, poster.imageCenter, itemView )
                loadPosterImage( poster.uuid, poster.imageRight, itemView )
            } else if(poster.imageLeft.isNotEmpty() && poster.imageCenter.isNotEmpty()){
                posterImageCenter.setCornerRadius(0F, radius, 0F, radius)
                loadPosterImage( poster.uuid, poster.imageLeft, itemView )
                loadPosterImage( poster.uuid, poster.imageCenter, itemView )
                posterImageRight.visibility = View.GONE
            } else if(poster.imageLeft.isNotEmpty()){
                posterImageCenter.visibility = View.GONE
                posterImageRight.visibility = View.GONE
                posterImageLeft.cornerRadius = radius
                loadPosterImage( poster.uuid, poster.imageLeft, itemView )
            } else {
                posterImageCenter.visibility = View.GONE
                posterImageRight.visibility = View.GONE
                posterImageLeft.visibility = View.GONE
            }
        }
    }

    private fun loadUserImage(userUid: String, itemView: View) {
        val load = storage.getReference("icon_image_users").child(userUid + "icon_image")
        load.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(itemView)
                .load(uri)
                .into(itemView.imageItemPoster)
        }.addOnFailureListener {
            Log.w("storage", it.message.toString())
        }
    }

    private fun  loadPosterImage(
        uuid : String,
        path: String,
        itemView : View
    ) {
        val sRef = storage.getReference("poster_$uuid").child(path)
        sRef.downloadUrl.addOnSuccessListener { uri ->

            if(path.contains(left.toString())){
                Glide.with(itemView)
                    .load(uri)
                    .into(itemView.imageLeftItemPoster)
            }
            if(path.contains(center.toString())){
                Glide.with(itemView)
                    .load(uri)
                    .into(itemView.imageCenterItemPoster)
            }
            if(path.contains(right.toString())){
                Glide.with(itemView)
                    .load(uri)
                    .into(itemView.imageRightItemPoster)
            }
        }.addOnFailureListener {
            Log.w("storage", it.message.toString())
        }
    }

}
