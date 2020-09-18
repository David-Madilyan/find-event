package com.madcryk.findevent.presenters

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.madcryk.findevent.R
import com.madcryk.findevent.activities.AuthActivity
import com.madcryk.findevent.fragments.LoginFragment
import com.madcryk.findevent.models.UserModel
import com.madcryk.findevent.showToast

class AuthPresenter(_view : AuthActivity){
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var view = _view
    private var db: DatabaseReference = Firebase.database.reference
    private var PREF_NAME: String? = view.getString(R.string.pref_name)

    private fun saveLoginData(email : String, pass : String){
        val sp = view.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString("user_email", email)
        editor.putString("user_password", pass)
        editor.apply()
    }

    private fun getLoginData(): Pair<String?, String?> {
        val sp = view.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val email = sp.getString("user_email", "")
        val pass = sp.getString("user_password", "")
        return Pair(email, pass)
    }

    private fun getCurrentUser(getUserListener : (UserModel?) -> Unit){
        val uid = auth.currentUser?.uid
        val map =  db.child("users").child(uid.toString())
        val userListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.w("loadUser:onCancelled", error.toException())
                getUserListener(null)
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.w("loadUser", " :success")
                val userModel = dataSnapshot.getValue< UserModel >()
                getUserListener(userModel)
            }
        }
        map.addListenerForSingleValueEvent(userListener)
    }

    private fun login(email : String, pass : String, loginListener : (UserModel?) -> Unit){
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(view) { task ->
                if (task.isSuccessful) {
                    Log.i("signIn user", "success")
                    getCurrentUser {
                            loginListener(it)
                    }
                } else {
                    Log.w("signIn user", task.exception.toString())
                    loginListener(null)
                }
            }.addOnFailureListener {
                loginListener(null)
            }
    }
    fun autoLoginUser() {
        val pairAuth = getLoginData()
        if(!pairAuth.first.isNullOrEmpty() && !pairAuth.second.isNullOrEmpty()){
            view.showAuthProcess()
            login(pairAuth.first.toString(), pairAuth.second.toString()) { userModel ->
                if(userModel != null){
                    if(userModel.city.isEmpty()){                                                   //сработает если не выбран город
                        view.hideAuthProcess()
                    }
                    view.setUserModel(userModel)
                    view.startMainActivity()
                }else{
                    view.hideAuthProcess()
                    view.showRegisterFragment()
                    view.showToast(view.getString(R.string.error_login))
                }
            }
        }else{
            view.hideAuthProcess()
            view.showRegisterFragment()
        }
    }

    fun loginFromFragment(mail: String, pass: String) {
        view.showAuthProcess()
        login(mail, pass) { userModel ->
            view.hideAuthProcess()

            if(userModel != null){
                saveLoginData(mail, pass)
                view.setUserModel(userModel)
                view.startMainActivity()
            }else{
                view.showAnyError()
            }
        }
    }

    private fun register(email : String, pass : String, registerListener: (String?) -> Unit){
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(view) { task ->
                if (task.isSuccessful) {
                    var uid = auth.currentUser?.uid
                    registerListener(uid)
                    Log.w("register", "success")
                } else {
                    Log.w("register", task.exception.toString())
                    registerListener(null)
                }
            }
    }

    fun registerFromFragment(mail: String, pass: String, phone: String, name : String) {
        view.showAuthProcess()
        register(mail, pass){ userUid ->
            view.hideAuthProcess()
            if(userUid != null){
                val userModel = UserModel( name, mail, userUid, "", 0.0,0.0,0, 0, 0,  phone)
                view.setUserModel(userModel)
                addUserInDatabase(userModel){
                    if(it) {
                        view.makeCurrentFragment(LoginFragment())
                        view.hideRegisterFragment()
                    } else
                        view.showToast(view.getString(R.string.create_error))
                }
            }
        }
    }

    fun recoveryPassFromFragment(mail: String) {
        view.showAuthProcess()
        recoveryPass(mail){
            view.hideAuthProcess()
            if(it){
                view.makeCurrentFragment(LoginFragment())
            }else{
                view.showToast(view.getString(R.string.is_not_working))

            }
        }
    }
    private fun recoveryPass(email : String, recoveryListener : (Boolean) -> Unit){
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(view){ task ->
                if (task.isSuccessful) {
                    Log.i("forgotPass", "success")
                    recoveryListener(true)
                } else {
                    Log.w("forgotPass", "failed")
                    recoveryListener(true)
                }
            }
    }

    private fun addUserInDatabase(userModel: UserModel, createUserListener: (Boolean) -> Unit) {
        db.child("users")
            .child(userModel.uid).setValue(userModel).addOnSuccessListener {
                Log.w("dbFirebase", "success")
                createUserListener(true)
            }
            .addOnFailureListener {
                Log.w("dbFirebase", "failed")
                createUserListener(false)
            }
    }
}