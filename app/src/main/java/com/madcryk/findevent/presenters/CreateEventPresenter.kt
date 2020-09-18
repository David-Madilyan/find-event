package com.madcryk.findevent.presenters

import co.metalab.asyncawait.async
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.madcryk.findevent.fragments.CreateEventFragment
import com.madcryk.findevent.models.EventModel
import com.madcryk.findevent.models.UserModel


@Suppress("DEPRECATION")
class CreateEventPresenter(_view : CreateEventFragment) {
    private var view = _view
    private var dbRef: DatabaseReference = Firebase.database.reference
    private var user = UserModel.instance
    fun addNewEvent(event : EventModel){
        async {
            dbRef.child("users").child(user.uid).child("events").setValue(++user.events)
            dbRef.child("events_list").child(event.uuid).setValue(event)
                .addOnCanceledListener {
                    view.canceledAdd()
                }
                .addOnSuccessListener {
                    view.successAdd()
                }
        }
    }
}