package com.madcryk.findevent.presenters

import android.graphics.Bitmap
import android.util.Log
import co.metalab.asyncawait.async
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.madcryk.findevent.fragments.ProfileFragment
import com.madcryk.findevent.models.EventModel
import com.madcryk.findevent.models.PosterModel
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream

class ProfilePresenter(_view : ProfileFragment) {
    private var view = _view
    private var storage = FirebaseStorage.getInstance()
    private var dbRef: DatabaseReference = Firebase.database.reference

    fun loadUserImage( userUid : String ){
        val load = storage.getReference("icon_image_users").child(userUid + "icon_image")
        load.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(view)
                .load(uri)
                .into(view.userImageProfile)
        }.addOnFailureListener {
            Log.w("storage", it.message.toString())
        }
    }

    fun uploadUserImage(bitmap: Bitmap, userUid : String){
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos)
        val byteArray = baos.toByteArray()
        if(byteArray.isNotEmpty()){
            async {
                val up =
                    await {
                        storage.getReference("icon_image_users").child(userUid + "icon_image").putBytes(byteArray)
                    }
                up.addOnFailureListener {
                    view.showFailureMessage()
                }.addOnSuccessListener {
                    view.showSuccessMessage()
                }
            }
        }
    }

    fun uploadMyPosters(uid: String) {
        val postersQuery = dbRef.child("posters_list").limitToFirst(100).orderByChild("userUid").equalTo(uid)
        val postersList : ArrayList<PosterModel> = ArrayList()
        async {
            postersQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.w("response", error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (itemSnapshot in snapshot.children) {
                        postersList.add(itemSnapshot.getValue<PosterModel>() as PosterModel)
                    }
                    view.completeLoadPosters(postersList)
                }

            })
        }
    }

    fun uploadMyEvents(uid: String) {
        val eventsQuery = dbRef.child("events_list").limitToFirst(100).orderByChild("userUuid").equalTo(uid)
        val eventsList : ArrayList<EventModel> = ArrayList()
        async {
            eventsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.w("response", error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue<EventModel>() as EventModel
                        eventsList.add(item)
                    }
                    view.completeLoadListEvents(eventsList)
                }

            })
        }
    }
}