package com.madcryk.findevent.presenters

import android.util.Log
import co.metalab.asyncawait.async
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.madcryk.findevent.activities.EditProfileActivity
import com.madcryk.findevent.models.UserModel

class EditProfilePresenter(_view : EditProfileActivity) {
    private var view = _view
    private var db: DatabaseReference = Firebase.database.reference

    fun updateUserModel(userModel: UserModel){
        async {
            db.child("users")
                .child(userModel.uid).setValue(userModel)
                .addOnSuccessListener {
                    Log.w("dbFirebase", "success")
                    view.successUpdateUser(userModel)
                }
                .addOnFailureListener {
                    Log.w("dbFirebase", it.message.toString())
                    view.failureUpdateUser()
                }
        }
    }
}