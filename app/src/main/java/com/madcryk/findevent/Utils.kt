package com.madcryk.findevent

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Context.showToast(text: String, duration : Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, text, duration).show()
}

fun isValidEmail(email: String): Boolean {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun Activity.getRootView(): View {
    return findViewById<View>(android.R.id.content)
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

@SuppressLint("SimpleDateFormat")
fun getCurrentTime(): String? {
    val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val date = Date()
    return dateFormat.format(date)
}