package com.madcryk.findevent.activities

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.madcryk.findevent.R
import com.madcryk.findevent.showToast
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.dialog_open_gps_settings.view.*
import kotlinx.android.synthetic.main.view_search.view.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MapActivity : AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {

    private var isGpsActive = false
    private var fineLocationGranted = false
    private var coarseLocationGranted = false
    private var resultCode = 200
    private var googleMap: GoogleMap? = null
    private var selectedPoint : LatLng? = null
    private var selectedAddress = ""
    private lateinit var locationManager : LocationManager
    private lateinit var sp : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        initPermissionsLocation()
    }

    override fun onDestroy() {
        if(googleMap != null){
            googleMap!!.clear()
        }
        super.onDestroy()
    }

    private fun initViews() {
        searchViewPlace.execute_search_button.setOnClickListener {
            val searchText = searchViewPlace.search_input_text.text.toString()
            if(searchText.isNotEmpty()){
                executeSearchPlace(searchText)
            }
        }
        choiceLocationButton.setOnClickListener {
            val intent = this.intent
            val type = intent.getStringExtra("location")
            if(selectedPoint != null && selectedAddress.isNotEmpty()){
                if(type == "event"){
                    sp = this.getSharedPreferences("create_event_fragment", Context.MODE_PRIVATE)
                    sp.edit()
                        .putFloat("event_latitude", selectedPoint?.latitude?.toFloat()!!)
                        .putFloat("event_longitude", selectedPoint?.longitude?.toFloat()!!)
                        .putString("event_address", selectedAddress)
                        .apply()
                }else
                    if(type ==  "poster"){
                        sp = this.getSharedPreferences("create_poster_fragment", Context.MODE_PRIVATE)
                        sp.edit()
                            .putFloat("poster_latitude", selectedPoint?.latitude?.toFloat()!!)
                            .putFloat("poster_longitude", selectedPoint?.longitude?.toFloat()!!)
                            .putString("poster_address", selectedAddress)
                            .apply()

                    }
                finish()
            }else
                showToast(getString(R.string.choice_place_map))
        }
    }

    private fun initMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    private fun executeSearchPlace(searchText: String) {
        val geocoder = Geocoder(this)
        var listAddress: List<Address>? = null
        try {
            listAddress = geocoder.getFromLocationName(searchText, 1)

        }catch (e : IOException){
            showToast(getString(R.string.any_error))
        }

        if(listAddress?.isNotEmpty()!! && googleMap != null){
            val address = listAddress[0]
            selectedPoint = LatLng(address.latitude, address.longitude)
            selectedAddress =
                """${address.locality}, ${address.thoroughfare}, ${address.featureName}"""
            val userMarker = MarkerOptions()
            userMarker.position(selectedPoint!!)
            userMarker.title(selectedAddress)
            findAddress.text = selectedAddress
            googleMap!!.clear()
            googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPoint, 15f))
            googleMap!!.addMarker(userMarker)
        }
    }

    private fun initPermissionsLocation() {
        fineLocationGranted = ContextCompat.checkSelfPermission(
            Objects.requireNonNull(this),
            permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        coarseLocationGranted = ContextCompat.checkSelfPermission(
            Objects.requireNonNull(this),
            permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        locationManager = Objects.requireNonNull<Context>(this)
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isGpsActive = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if(!fineLocationGranted || !coarseLocationGranted){
            ActivityCompat.requestPermissions(
                Objects.requireNonNull(this), arrayOf(
                    permission.ACCESS_FINE_LOCATION,
                    permission.ACCESS_COARSE_LOCATION
                ),
                resultCode
            )
        }
        if(!isGpsActive) { showDialogActiveGps() } else initMap()
    }

    private fun showDialogActiveGps() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_open_gps_settings, null)
        val mBuilder = AlertDialog
            .Builder(this, R.style.hint_dialog_style)
            .setView(mDialogView)
            .show()

        mDialogView.openLocationSettings.setOnClickListener {
            startActivityForResult(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                201
            )
            mBuilder.dismiss()
        }

        mDialogView.closeDialogLocation.setOnClickListener {
            initMap()
            mBuilder.dismiss()
        }
        mBuilder.setOnDismissListener {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0 && requestCode == 201) {
            isGpsActive = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (fineLocationGranted && coarseLocationGranted && isGpsActive) {
                initMap()
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    Objects.requireNonNull(this), arrayOf(
                        permission.ACCESS_FINE_LOCATION,
                        permission.ACCESS_COARSE_LOCATION
                    ),
                    resultCode
                )
            }else{
                initMap()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (resultCode == requestCode) {
            if (grantResults[0] == 0 && grantResults[1] == 0) {
                fineLocationGranted = true
                coarseLocationGranted = true
                if(isGpsActive) initMap()
            }else {
                finish()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(_googleMap: GoogleMap?) {
        googleMap = _googleMap
        if(fineLocationGranted && coarseLocationGranted){
            locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if(isGpsActive){
                googleMap!!.isMyLocationEnabled = true
                googleMap!!.setOnMyLocationButtonClickListener(this)
                googleMap!!.setOnMyLocationClickListener(this)

            }else{
                val point = LatLng(55.753960, 37.620393)                                            // moscow
                selectedPoint = point
                val userMarker = MarkerOptions()
                userMarker.position(point)
                userMarker.title("Какое то произвольное место")
                googleMap!!.clear()
                googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15f))
                googleMap!!.addMarker(userMarker)
            }

            googleMap!!.setOnMapClickListener { latLng ->
                val geocoder = Geocoder(this)
                selectedPoint =  latLng
                var list : List<Address>? = null
                try {
                    list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                }catch (e : IOException){
                    showToast(getString(R.string.any_error))
                }
                if (list != null && list.isNotEmpty()) {
                    selectedAddress =
                        """${list[0].locality}, ${list[0].thoroughfare}, ${list[0].featureName}"""
                    findAddress.text = selectedAddress
                }
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title(selectedAddress)
                googleMap!!.clear()
                googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                googleMap!!.addMarker(markerOptions)                                                // Add Marker on Map
            }
            initViews()
        }
    }


    override fun onMyLocationButtonClick(): Boolean {
        if(googleMap != null){
            val geocoder = Geocoder(this)
            if(googleMap!!.myLocation != null){
                selectedPoint =  LatLng(googleMap!!.myLocation.latitude, googleMap!!.myLocation.longitude)
                var list : List<Address>? = null
                try {
                    list = geocoder.getFromLocation(selectedPoint!!.latitude, selectedPoint!!.longitude, 1)
                }catch (e : IOException){
                    showToast(getString(R.string.any_error))
                }
                if (list != null && list.isNotEmpty()) {
                    selectedAddress =
                        """${list[0].locality}, ${list[0].thoroughfare}, ${list[0].featureName}"""
                    findAddress.text = selectedAddress
                    googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPoint, 15f))
                }
            }
        }
        return false
    }

    override fun onMyLocationClick(location : Location) {
        selectedPoint = LatLng(location.latitude, location.longitude)
        googleMap!!.clear()
        googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPoint, 15f))
    }
}