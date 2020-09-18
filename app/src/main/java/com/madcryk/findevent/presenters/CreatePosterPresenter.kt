package com.madcryk.findevent.presenters

import android.graphics.Bitmap
import co.metalab.asyncawait.async
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.madcryk.findevent.fragments.CreatePosterFragment
import com.madcryk.findevent.models.PosterModel
import com.madcryk.findevent.models.UserModel
import java.io.ByteArrayOutputStream

@Suppress("DEPRECATION")
class CreatePosterPresenter(_view : CreatePosterFragment) {
    private var view = _view
    private var dbRef: DatabaseReference = Firebase.database.reference
    private var storage = FirebaseStorage.getInstance()
    private var user = UserModel.instance
    fun addNewPoster(poster: PosterModel) {
        async {
            dbRef.child("users").child(user.uid).child("posters").setValue(++user.posters)
            dbRef.child("posters_list").child(poster.uuid).setValue(poster)
                .addOnCanceledListener {
                    view.canceledAdd()
                }
                .addOnSuccessListener {
                    view.successAdd()
                }
        }
    }

    fun uploadImagePoster(bitmap: Bitmap, fileName: String, posterUuid : String) {

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val byteArray = baos.toByteArray()
        if(byteArray.isNotEmpty()){
            async {
                val up =
                await {
                    storage.getReference("poster_$posterUuid").child(fileName).putBytes(byteArray)
                }
                up.addOnFailureListener {
                    view.showFailureMessage(fileName)
                }.addOnSuccessListener {
                    view.showSuccessMessage()
                }
            }
        }

    }
}