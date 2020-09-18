package com.madcryk.findevent.presenters

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.madcryk.findevent.fragments.SelectCityFragment
import com.madcryk.findevent.models.UserModel

class SelectCityPresenter(_view : SelectCityFragment) {
    private var view = _view
    private var db: DatabaseReference = Firebase.database.reference

    fun updateUser(u: UserModel){
        db.child("users")
            .child(u.uid).setValue(u).addOnSuccessListener {
                Log.w("dbFirebase", "success")
                view.successUpdateUser()
            }
            .addOnFailureListener {
                Log.w("dbFirebase", "failed")
                view.failureUpdateUser()
            }
    }
}