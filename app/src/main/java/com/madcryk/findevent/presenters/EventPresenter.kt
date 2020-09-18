package com.madcryk.findevent.presenters

import co.metalab.asyncawait.async
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.madcryk.findevent.activities.EventActivity
import com.madcryk.findevent.models.EventModel

class EventPresenter (_view : EventActivity){
    private var view = _view
    private var dbRef: DatabaseReference = Firebase.database.reference

    //    async
    fun addUserInEvent(uuid: String, userUid: String, count : Int){
        async {
            dbRef.child("events_list").child(uuid).child("active_users").child(userUid).setValue(true)
                .addOnCanceledListener {
                    view.failureAddUser()
                }
                .addOnSuccessListener {
                    view.checkEventFollowing(true)
                    dbRef.child("events_list").child(uuid).child("count").setValue(count)
                    view.successAddUser()
                }
        }
    }

    fun changeCountFollowersListener(uuid: String){
        dbRef.child("events_list").child(uuid).child("count")
            .addValueEventListener( object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    val count = snapshot.value as Long
                    view.updateCountFollowers(count)
                }

            })
    }

    //    async
    fun loadEvent(uuid: String?, userUid: String) {
        var event: EventModel
        view.showProgressBar()
        async {
            dbRef.child("events_list").child(uuid.toString())
                .addListenerForSingleValueEvent( object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    view.hideProgressBar()
                    view.checkEventFollowing(false)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    view.hideProgressBar()
                    event = snapshot.getValue<EventModel>() as EventModel
                    dbRef.child("events_list").child(uuid.toString()).child("active_users").orderByChild(userUid)
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(error: DatabaseError) {}
                            override fun onDataChange(snapshot: DataSnapshot) {
                                view.hideProgressBar()
                                if (snapshot.child(userUid).value != null){
                                    val status = snapshot.child(userUid).value as Boolean
                                    view.checkEventFollowing(status)
                                }else {
                                    view.checkEventFollowing(false)
                                }
                                view.initViews(event)
                            }

                        })
                }

            })
        }

    }

    fun listenerCountFollowers(uuid: String) {
        dbRef.child("events_list").child(uuid).child("count")
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value != null){
                        view.setCountFollowers(snapshot.value as Long)
                    }
                }

            })
    }

    fun disconnect() {
        dbRef.onDisconnect()
    }

    fun resumeConnect() {
        dbRef = Firebase.database.reference
    }
}