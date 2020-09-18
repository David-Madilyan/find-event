package com.madcryk.findevent.presenters

import android.util.Log
import co.metalab.asyncawait.async
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.madcryk.findevent.fragments.PostersFragment
import com.madcryk.findevent.models.PosterModel

class PostersPresenter(_view : PostersFragment) {
    private var view = _view
    private var dbRef: DatabaseReference = Firebase.database.reference
    private var postersList : ArrayList<PosterModel> = ArrayList()

    fun loadRangeListPosters() {
        val postersQuery = dbRef.child("posters_list").limitToFirst(1000)
        postersList = ArrayList()
        async {
            postersQuery.addListenerForSingleValueEvent(object : ValueEventListener{
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

    fun refreshListPosters() {
        val postersQuery = dbRef.child("posters_list").limitToFirst(1000)
        postersList = ArrayList()
        async {
            postersQuery.addListenerForSingleValueEvent( object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.w("response", error.message)
                }

                override fun onDataChange( snapshot: DataSnapshot ) {
                    for ( itemSnapshot in snapshot.children ) {
                        postersList.add(itemSnapshot.getValue< PosterModel >() as PosterModel)
                    }
                    view.completeRefreshListPosters( postersList )
                }

            })
        }
    }

    fun findPostersByTitleDesc(s: String) {
        val titleFetchQuery = dbRef.child("posters_list").orderByChild("title").equalTo(s)
        postersList = ArrayList()
        async {
            titleFetchQuery.addListenerForSingleValueEvent( object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.w("response", error.message)
                }

                override fun onDataChange( snapshot: DataSnapshot ) {
                    for ( itemSnapshot in snapshot.children ) {
                        postersList.add(itemSnapshot.getValue< PosterModel >() as PosterModel)
                    }
                    view.completeSearchPosters( postersList )
                }
            })
        }
    }
}
