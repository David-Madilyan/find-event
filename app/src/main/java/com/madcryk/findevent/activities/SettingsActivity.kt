package com.madcryk.findevent.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.madcryk.findevent.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var PREF_NAME: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        PREF_NAME = getString(R.string.pref_name)
        initViews()
    }

    private fun initViews() {
        closeSettingButton.setOnClickListener {
            finish()
        }
        exitAccountButton.setOnClickListener {
            val sp = applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            sp.edit().remove("user_email").remove("user_password").apply()
            startActivity(Intent(this, AuthActivity::class.java))
        }
        notificationSwitcher.setOnCheckedChangeListener { _, isChecked ->
            val sp = applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            sp.edit().putBoolean("notification", isChecked).apply()
        }
    }
}