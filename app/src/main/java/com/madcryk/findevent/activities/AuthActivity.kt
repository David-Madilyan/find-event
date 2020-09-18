package com.madcryk.findevent.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.madcryk.findevent.R
import com.madcryk.findevent.fragments.SelectCityFragment
import com.madcryk.findevent.fragments.LoginFragment
import com.madcryk.findevent.fragments.RecoveryPassFragment
import com.madcryk.findevent.fragments.RegisterFragment
import com.madcryk.findevent.models.UserModel
import com.madcryk.findevent.presenters.AuthPresenter
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*

class AuthActivity : AppCompatActivity() {
    private lateinit var authPresenter : AuthPresenter
    private lateinit var userModel : UserModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        userModel = UserModel.instance
        authPresenter = AuthPresenter(_view = this)
        authPresenter.autoLoginUser()

        initViews()
    }

    fun showRegisterFragment(){
        makeCurrentFragment(RegisterFragment())
    }

    private fun initViews() {
        goLoginFragment.setOnClickListener {
            makeCurrentFragment(LoginFragment())
            goLoginFragment.visibility = View.INVISIBLE
            forgotLayout.visibility = View.VISIBLE
            fragmentRegister.visibility = View.GONE
        }
        createAccountBtn.setOnClickListener {
            makeCurrentFragment(RegisterFragment())
            forgotLayout.visibility = View.INVISIBLE
            goLoginFragment.visibility = View.VISIBLE
            fragmentLogin.visibility = View.GONE
        }
        forgotPassBtn.setOnClickListener {
            makeCurrentFragment(RecoveryPassFragment())
            fragmentLogin.visibility = View.GONE
        }
    }
    fun showAuthProcess(){
        authContainerLayout.visibility = View.INVISIBLE
        titleLayout.visibility = View.INVISIBLE
        logo_title.visibility = View.VISIBLE
        authParentLayout.setBackgroundResource(R.drawable.background)
    }
    fun hideAuthProcess(){
        authContainerLayout.visibility = View.VISIBLE
        titleLayout.visibility = View.VISIBLE
        logo_title.visibility = View.GONE
        authParentLayout.setBackgroundResource(R.color.backgroundColor)
    }
    fun startMainActivity(){
        if(userModel.city.isNotEmpty()){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }else{
            makeCurrentFragment(SelectCityFragment())
        }
    }

    fun setUserModel(user: UserModel){
        userModel.age = user.age
        userModel.city = user.city
        userModel.email = user.email
        userModel.phone = user.phone
        userModel.uid = user.uid
        userModel.name = user.name
        userModel.longitude = user.longitude
        userModel.latitude = user.latitude
        userModel.posters = user.posters
        userModel.events = user.events

    }

    fun showAnyError(){
        errorTextView.visibility = View.VISIBLE
        errorTextView.text = getString(R.string.notFoundRecord)
    }

    fun makeCurrentFragment(fragment : Fragment){
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragmentHostAuth, fragment, fragment.javaClass.simpleName)
            .commit()
    }

    fun hideRegisterFragment() {
        goLoginFragment.visibility = View.INVISIBLE
        forgotLayout.visibility = View.VISIBLE
        fragmentRegister.visibility = View.GONE
    }
}