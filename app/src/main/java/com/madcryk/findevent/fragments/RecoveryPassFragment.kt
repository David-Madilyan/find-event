package com.madcryk.findevent.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.madcryk.findevent.R
import com.madcryk.findevent.activities.AuthActivity
import com.madcryk.findevent.isValidEmail
import com.madcryk.findevent.presenters.AuthPresenter
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.emailEditText
import kotlinx.android.synthetic.main.fragment_recovery_pass.*


class RecoveryPassFragment : Fragment() {
    private lateinit var authPresenter: AuthPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recovery_pass, container, false)
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
                    recoveryButton.isEnabled = true
                } else {
                    recoveryButton.isEnabled = false
                    emailEditText.setBackgroundResource(R.drawable.edit_error)
                    emailEditText.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_error_email,0)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        recoveryButton.setOnClickListener {
            if(it.isEnabled){
                authPresenter.recoveryPassFromFragment(emailEditText.text.toString())
            }
        }

    }
}