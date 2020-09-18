package com.madcryk.findevent.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.format.DateUtils.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.madcryk.findevent.R
import com.madcryk.findevent.activities.MapActivity
import com.madcryk.findevent.getCurrentTime
import com.madcryk.findevent.models.EventModel
import com.madcryk.findevent.models.UserModel
import com.madcryk.findevent.presenters.CreateEventPresenter
import com.madcryk.findevent.showToast
import kotlinx.android.synthetic.main.dialog_hint_create.view.*
import kotlinx.android.synthetic.main.fragment_create_event.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*


class CreateEventFragment : Fragment() {
    private lateinit var sp : SharedPreferences
    private lateinit var event : EventModel
    private lateinit var eventPresenter : CreateEventPresenter
    private var dateAndTime = getInstance()
    private var userModel = UserModel.instance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_event, container, false)
    }

    override fun onResume() {                                                                       //если этот метод не будет вызываться после выбора адреса с MapActivity, то нужно изменить на другой
        super.onResume()
        sp = requireContext().getSharedPreferences("create_event_fragment", Context.MODE_PRIVATE)
        val latitude = sp.getFloat("event_latitude", 0.0F)
        val longitude = sp.getFloat("event_longitude", 0.0F)
        val address = sp.getString("event_address", "")
        if(address?.isNotEmpty()!!){
            addressEventCreate.visibility = View.VISIBLE
            addressEventCreate.text = address.toString()
            event.address = address.toString()
            event.latitude = latitude.toDouble()
            event.longitude = longitude.toDouble()
            sp.edit()
                .remove("event_latitude")
                .remove("event_longitude")
                .remove("event_address")
                .apply()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        event = EventModel()
        eventPresenter = CreateEventPresenter(this)
        initViews()
    }

    private fun initViews() {

        locationButton.setOnClickListener {
            val intent = Intent(requireContext(), MapActivity::class.java)
            intent.putExtra("location", "event")
            startActivity(intent)
        }
        datapickerButton.setOnClickListener {
            timeEventCreate.text = ""
            setDate()
        }
        addEventButton.setOnClickListener {
            if(checkInputFields()){
                event.timeCreate = getCurrentTime().toString()
                errorTextViewCreate.visibility = View.INVISIBLE
                it.isClickable = false
                it.isEnabled = false
                showProgressBar()
                eventPresenter.addNewEvent(event)
            }else{
                errorTextViewCreate.visibility = View.VISIBLE
            }
        }
    }


    private fun checkInputFields() : Boolean {
        event.userUuid = userModel.uid
        event.userName = userModel.name
        event.title = titleCreateEvent.text.toString()
        event.body = descriptionEventCreate.text.toString()
        event.uuid = UUID.randomUUID().toString()
        event.time = timeEventCreate.text.toString()
        if(countPersonEventCreate.text.toString().isNotEmpty()){
            event.maxPersons = countPersonEventCreate.text.toString().toInt()
        }
        if(checkBoxIsPhone.isChecked){
            event.isPhone = true
            event.userPhone = userModel.phone
        }
        if(event.title.isEmpty()){
            errorTextViewCreate.text = getString(R.string.not_found_title)
            return false
        }
        if(event.body.isEmpty()){
            errorTextViewCreate.text = getString(R.string.not_found_description)
            return false
        }
        if(event.maxPersons == 0){
            errorTextViewCreate.text = getString(R.string.not_found_count)
            return false
        }
        if(event.address.isEmpty() || event.latitude == 0.0 || event.longitude == 0.0){
            errorTextViewCreate.text = getString(R.string.not_found_location)
            return false
        }
        if(event.time.isEmpty()){
            errorTextViewCreate.text = getString(R.string.not_found_date)
            return false
        }
        return true
    }

    // отображаем диалоговое окно для выбора даты
    private fun setDate() {
        DatePickerDialog(
            requireContext(), d,
            dateAndTime.get(YEAR),
            dateAndTime.get(MONTH),
            dateAndTime.get(DAY_OF_MONTH)
        )
            .show()
    }

    private fun setTime() {
        TimePickerDialog(
            requireContext(), t,
            dateAndTime.get(HOUR_OF_DAY),
            dateAndTime.get(MINUTE), true
        )
            .show()
    }

    private var t =
        OnTimeSetListener { view, hourOfDay, minute ->
            dateAndTime.set(HOUR_OF_DAY, hourOfDay)
            dateAndTime.set(MINUTE, minute)
            setInitialDateTime()
        }
    private var d =
        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            dateAndTime.set(YEAR, year)
            dateAndTime.set(MONTH, monthOfYear)
            dateAndTime.set(DAY_OF_MONTH, dayOfMonth)
            setTime()

        }

    @SuppressLint("SetTextI18n")
    private fun setInitialDateTime() {
        timeEventCreate.text = timeEventCreate.text.toString() + " " + formatDateTime(
            requireContext(),
            dateAndTime.timeInMillis,
            FORMAT_SHOW_DATE or  FORMAT_SHOW_YEAR or  FORMAT_SHOW_TIME
        )
        if(!timeEventCreate.text.isNullOrEmpty()){
            timeEventCreate.visibility = View.VISIBLE
        }
    }


    fun successAdd() {
        hideProgressBar()
        addEventButton.isEnabled = true
        addEventButton.isClickable = true
        context?.showToast(getString(R.string.event_added))
    }

    fun canceledAdd() {
        hideProgressBar()
        addEventButton.isEnabled = true
        addEventButton.isClickable = true
        context?.showToast(getString(R.string.canceled))
    }
    private fun showProgressBar(){
        createEventProgressBar.visibility = View.VISIBLE
    }
    private fun hideProgressBar(){
        createEventProgressBar.visibility = View.GONE
    }
}