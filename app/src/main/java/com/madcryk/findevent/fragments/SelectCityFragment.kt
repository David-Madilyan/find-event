package com.madcryk.findevent.fragments

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import com.madcryk.findevent.R
import com.madcryk.findevent.activities.MainActivity
import com.madcryk.findevent.models.UserModel
import com.madcryk.findevent.presenters.SelectCityPresenter
import com.madcryk.findevent.showToast
import kotlinx.android.synthetic.main.fragment_select_city.*
import java.io.IOException

class SelectCityFragment : Fragment() {

    private lateinit var userModel : UserModel
    private lateinit var selectPresenter : SelectCityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_city, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userModel = UserModel.instance
        selectPresenter = SelectCityPresenter(this)
        initViews()
    }

    private fun initViews() {
        selectButtonCity.setOnClickListener {
            checkInGeocoder()
        }
    }

    private fun checkInGeocoder() {
        checkImageCity.visibility = View.INVISIBLE
        errorSelectCity.visibility = View.INVISIBLE
        val geocoder = Geocoder(requireContext())
        var list: List<Address>? = null
        try {
            if (editSelectCity.text.toString().isNotEmpty()) {
                list = geocoder.getFromLocationName(editSelectCity.text.toString(), 5)
            }
        } catch (e: IOException) {
            showError()
        }
        if(!list.isNullOrEmpty()){
            checkImageCity.setImageResource(R.drawable.ic_check)
            checkImageCity.visibility = View.VISIBLE
            userModel.city = list[0].featureName
            userModel.latitude = list[0].latitude
            userModel.longitude = list[0].longitude
            editSelectCity.setText(list[0].featureName.toString())
            selectPresenter.updateUser(userModel)
        }else{
            showError()
        }
    }

    private fun showError() {
        errorSelectCity.text = getString(R.string.error_city)
        errorSelectCity.visibility = View.VISIBLE
        checkImageCity.visibility = View.VISIBLE
        checkImageCity.setImageResource(R.drawable.ic_close)
    }

    fun successUpdateUser() {
        context?.startActivity(Intent(requireContext(), MainActivity::class.java))
        activity?.finish()
    }

    fun failureUpdateUser() {
        context?.showToast("Возникла ошибка при выборе города. Выберите город в настройках проифля.")
        context?.startActivity(Intent(requireContext(), MainActivity::class.java))
    }
}