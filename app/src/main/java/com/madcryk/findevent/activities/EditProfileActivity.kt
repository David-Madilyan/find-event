package com.madcryk.findevent.activities

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.madcryk.findevent.R
import com.madcryk.findevent.isValidEmail
import com.madcryk.findevent.models.UserModel
import com.madcryk.findevent.presenters.EditProfilePresenter
import com.madcryk.findevent.showToast
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.IOException

class EditProfileActivity : AppCompatActivity() {

    private lateinit var user  : UserModel
    private val newUser = UserModel()
    private lateinit var PREF_NAME: String
    private lateinit var  epPresenter: EditProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        PREF_NAME = getString(R.string.pref_name)
        user  = UserModel.instance
        initViews()
    }

    private fun initViews() {
        epPresenter = EditProfilePresenter(this)
        setUserDataFromModel()

        closeButtonActivity.setOnClickListener {
            finish()
        }

        saveButtonEditProfile.setOnClickListener {
            errorTextEditProfile.visibility = View.INVISIBLE
            errorTextEditProfile.setTextColor(getColor(R.color.errorStrokeColor))

            if(checkInputFields()){
                newUser.events = user.events
                newUser.posters = user.posters
                newUser.uid = user.uid
                epPresenter.updateUserModel(newUser)
            }
        }
    }

    private fun checkInputFields() : Boolean {
        if (editUsername.text.toString().isNotEmpty()) {
            newUser.name = editUsername.text.toString()
        } else {
            errorTextEditProfile.text = getString(R.string.error_name)
            errorTextEditProfile.visibility = View.VISIBLE
            return false
        }
        if (isValidEmail(editEmail.text.toString())) {
            newUser.email = editEmail.text.toString()
        } else {
            errorTextEditProfile.text = getString(R.string.error_email)
            errorTextEditProfile.visibility = View.VISIBLE
            return false
        }
        if (editAge.text.toString().isNotEmpty()) {
            if(editAge.text.toString().toInt() > 0){
                newUser.age = editAge.text.toString().toInt()
            }else{
                errorTextEditProfile.text = getString(R.string.error_age)
                errorTextEditProfile.visibility = View.VISIBLE
                return false
            }
        }else newUser.age = 0
        if (editPhone.text.toString().length < 16) {
            errorTextEditProfile.text = getString(R.string.error_phone)
            errorTextEditProfile.visibility = View.VISIBLE
            return false
        }else {
            newUser.phone = editPhone.text.toString()
        }
        val geocoder = Geocoder(this)
        var list: List<Address>? = null
        try {
            if (editCity.text.toString().isNotEmpty()) {
                list = geocoder.getFromLocationName(editCity.text.toString(), 5)
            }
        } catch (e: IOException) {
            errorTextEditProfile.text = getString(R.string.error_city)
            errorTextEditProfile.visibility = View.VISIBLE
            return false
        }

        if (list.isNullOrEmpty()) {
            errorTextEditProfile.text = getString(R.string.error_city)
            errorTextEditProfile.visibility = View.VISIBLE
            return false
        } else {
            newUser.city = list[0].featureName.toString()
            newUser.longitude = list[0].longitude
            newUser.latitude = list[0].latitude
            editCity.setText(list[0].featureName.toString())
        }
        return true
    }

    private fun setUserDataFromModel() {
        editUsername.setText(user.name)
        editEmail.setText(user.email)
        if(user.age == 0){
            editAge.hint = getString(R.string.not_indicat)
        }else {
            editAge.setText(user.age.toString())
        }
        if (user.phone.isNotEmpty()){
            editPhone.setText(user.phone)
        }else {
            editPhone.hint = getString(R.string.not_indicat)
        }

        editCity.setText(user.city)
    }

    fun successUpdateUser(u: UserModel) {
        user.email = u.email
        user.name = u.name
        user.city = u.city
        user.phone = u.phone
        user.age = u.age
        user.uid = u.uid
        user.events = u.events
        user.posters = u.posters
        user.latitude = u.latitude
        user.longitude = u.longitude
        val sp =
            applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("user_email", user.email)
        errorTextEditProfile.visibility = View.VISIBLE
        errorTextEditProfile.setTextColor(getColor(R.color.blueColor))
        errorTextEditProfile.text = getString(R.string.update_success)
    }

    fun failureUpdateUser() {
        this.showToast(getString(R.string.error_upload))
    }
}