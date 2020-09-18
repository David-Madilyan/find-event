package com.madcryk.findevent.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.madcryk.findevent.R
import com.madcryk.findevent.activities.AuthActivity
import com.madcryk.findevent.isValidEmail
import com.madcryk.findevent.presenters.AuthPresenter
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {
    private lateinit var authPresenter: AuthPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authPresenter = AuthPresenter(requireActivity() as AuthActivity)
        initViews()
    }

    private fun initViews() {
        emailEditText.addTextChangedListener( object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(isValidEmail(s.toString())) {
                    emailEditText.setBackgroundResource(R.drawable.edit_focus)
                    emailEditText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_check_mail,0)
                } else {
                    emailEditText.setBackgroundResource(R.drawable.edit_error)
                    emailEditText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_error_email,0)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        passEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().length < 8){
                    passEditText.error = getString(R.string.errorPass1)
                    passEditText.setBackgroundResource(R.drawable.edit_error)
                }else{
                    passEditText.setBackgroundResource(R.drawable.edit_focus)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

        passShowHideButton.setOnClickListener {
            if(it.tag == "show"){
                passEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                passShowHideButton.setImageResource(R.drawable.ic_view_pass)
                it.tag = "hide"
                return@setOnClickListener
            }
            if(it.tag == "hide"){
                passEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                passShowHideButton.setImageResource(R.drawable.ic_hide_pass)
                it.tag = "show"
                return@setOnClickListener
            }
        }

        loginButton.setOnClickListener {
            errorTextView.visibility = View.GONE
            if(isValidEmail(emailEditText.text.toString()) && passEditText.text.length > 7){
                authPresenter.loginFromFragment(emailEditText.text.toString(), passEditText.text.toString())
            }
        }
    }

    private fun showProgressBar() {
        loginProgress.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        loginProgress.visibility = View.GONE
    }
}