package com.madcryk.findevent.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.madcryk.findevent.R
import com.madcryk.findevent.activities.AuthActivity
import com.madcryk.findevent.isValidEmail
import com.madcryk.findevent.presenters.AuthPresenter
import com.madcryk.findevent.showToast
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : Fragment() {
    private lateinit var authPresenter : AuthPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
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
        passEditText.transformationMethod = PasswordTransformationMethod.getInstance()
        passShowHide.setOnClickListener {
            if(it.tag == "show"){
                passEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                passShowHide.setImageResource(R.drawable.ic_view_pass)
                it.tag = "hide"
                return@setOnClickListener
            }
            if(it.tag == "hide"){
                passEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                passShowHide.setImageResource(R.drawable.ic_hide_pass)
                it.tag = "show"
                return@setOnClickListener
            }
        }
        passEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().length < 8){
                    passEditText.error = getString(R.string.errorPass1)
                    passEditText.setBackgroundResource(R.drawable.edit_error)
                }else{
                    passEditText.setBackgroundResource(R.drawable.edit_focus)
                }
                if(s.toString() == confirmPassEditText.text.toString() && s.toString().length > 7){
                    confirmPassEditText.setBackgroundResource(R.drawable.edit_focus)
                    passEditText.setBackgroundResource(R.drawable.edit_focus)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
        confirmPassEditText.addTextChangedListener( object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().length < 8){
                    confirmPassEditText.error = getString(R.string.errorPass1)
                    confirmPassEditText.setBackgroundResource(R.drawable.edit_error)
                }else{
                    confirmPassEditText.setBackgroundResource(R.drawable.edit_focus)
                }
                if(s.toString() != passEditText.text.toString()){
                    confirmPassEditText.error = getString(R.string.errorPass2)
                    confirmPassEditText.setBackgroundResource(R.drawable.edit_error)
                }else{
                    confirmPassEditText.setBackgroundResource(R.drawable.edit_focus)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

        confirmPassEditText.transformationMethod = PasswordTransformationMethod.getInstance()
        confirmHideShowButton.setOnClickListener {
            if(it.tag == "show"){
                confirmPassEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                confirmHideShowButton.setImageResource(R.drawable.ic_view_pass)
                it.tag = "hide"
                return@setOnClickListener
            }
            if(it.tag == "hide"){
                confirmPassEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                confirmHideShowButton.setImageResource(R.drawable.ic_hide_pass)
                it.tag = "show"
                return@setOnClickListener
            }
        }

        registerButton.setOnClickListener {
            if(isValidEmail(emailEditText.text.toString())
                && passEditText.text.toString() == confirmPassEditText.text.toString()
                && passEditText.text.length > 7){
                if(phoneEditText.text!!.length != 16){
                    phoneEditText.error = getString(R.string.requireField)
                    phoneEditText.setBackgroundResource(R.drawable.edit_error)
                    return@setOnClickListener
                }
                if(nameEditText.text.isNullOrEmpty()){
                    nameEditText.error = getString(R.string.requireField)
                    nameEditText.setBackgroundResource(R.drawable.edit_error)
                    return@setOnClickListener
                }
                authPresenter.registerFromFragment(emailEditText.text.toString(),
                    passEditText.text.toString(),
                    phoneEditText.text.toString(),
                    nameEditText.text.toString()
                )
            }else
                context?.showToast( getString(R.string.incorrectData))
        }
    }
}