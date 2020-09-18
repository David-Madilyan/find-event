package com.madcryk.findevent.fragments

import android.Manifest.permission.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.madcryk.findevent.R
import com.madcryk.findevent.activities.MapActivity
import com.madcryk.findevent.constants.PImage.*
import com.madcryk.findevent.getCurrentTime
import com.madcryk.findevent.models.PosterModel
import com.madcryk.findevent.models.UserModel
import com.madcryk.findevent.presenters.CreatePosterPresenter
import com.madcryk.findevent.showToast
import com.makeramen.roundedimageview.RoundedImageView
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.fragment_create_poster.*
import java.util.*
import kotlin.collections.ArrayList


class CreatePosterFragment : Fragment(){

    private lateinit var poster : PosterModel
    private lateinit var posterPresenter: CreatePosterPresenter
    private var userModel = UserModel.instance
    private lateinit var sp : SharedPreferences
    private var imagesUri : ArrayList<Uri> = ArrayList()
    private val CAMERA_PICK_CODE = 0

    override fun onResume() {                                                                       //если этот метод не будет вызываться после выбора адреса с MapActivity, то нужно изменить на другой
        super.onResume()
        sp = requireContext().getSharedPreferences("create_poster_fragment", Context.MODE_PRIVATE)
        val latitude = sp.getFloat("poster_latitude", 0.0F)
        val longitude = sp.getFloat("poster_longitude", 0.0F)
        val address = sp.getString("poster_address", "")
        if(address?.isNotEmpty()!!){
            addressPosterCreate.visibility = View.VISIBLE
            addressPosterCreate.text = address.toString()
            poster.address = address.toString()
            poster.latitude = latitude.toDouble()
            poster.longitude = longitude.toDouble()
            sp.edit()
                .remove("poster_latitude")
                .remove("poster_longitude")
                .remove("poster_address")
                .apply()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_poster, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        poster = PosterModel()
        poster.uuid = UUID.randomUUID().toString()
        posterPresenter = CreatePosterPresenter(this)
        initViews()
    }

    private fun initViews() {
        locationButton.setOnClickListener {
            val intent = Intent(requireContext(), MapActivity::class.java)
            intent.putExtra("location", "poster")
            startActivity(intent)
        }
        imageLeftItemPoster.setOnClickListener {
            startPickIntent()
        }
        imageCenterItemPoster.setOnClickListener {
            startPickIntent()
        }
        imageRightItemPoster.setOnClickListener {
            startPickIntent()
        }
        addPosterButton.setOnClickListener {
            if(checkInputFields()){
                it.isClickable = false
                it.isEnabled = false
                errorTextViewCreate.visibility = View.INVISIBLE
                showProgressBar()
                posterPresenter.addNewPoster(poster)
                uploadImagesPoster()
            }else{
                errorTextViewCreate.visibility = View.VISIBLE
            }
        }
    }

    private fun uploadImagesPoster() {
        if(poster.imageLeft.isNotEmpty()){
            val bitmap =
                MediaStore.Images.Media.getBitmap(activity?.contentResolver, imagesUri[0])
            posterPresenter.uploadImagePoster(bitmap, poster.imageLeft, poster.uuid)
        }
        if(poster.imageCenter.isNotEmpty()){
            val bitmap =
                MediaStore.Images.Media.getBitmap(activity?.contentResolver, imagesUri[1])
            posterPresenter.uploadImagePoster(bitmap, poster.imageCenter, poster.uuid)
        }
        if(poster.imageRight.isNotEmpty()){
            val bitmap =
                MediaStore.Images.Media.getBitmap(activity?.contentResolver, imagesUri[2])
            posterPresenter.uploadImagePoster(bitmap, poster.imageRight, poster.uuid)
        }
    }

    private fun startPickIntent(){
        if (checkSelfPermission( requireContext(), READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission( requireContext(), CAMERA) == PackageManager.PERMISSION_GRANTED){
            showImagePicker()
        }else{
            val permissions = arrayOf(CAMERA, READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions( requireActivity(), permissions, CAMERA_PICK_CODE)
        }

    }

    private fun showImagePicker() {
        ImagePicker.with(this)
            .setFolderMode(true)
            .setFolderTitle(getString(R.string.gallery))
            .setRootDirectoryName(Config.ROOT_DIR_DCIM)
            .setDirectoryName(getString(R.string.choise_images))
            .setMultipleMode(true)
            .setDoneTitle(getString(R.string.choice))
            .setStatusBarColor("#609EE8")
            .setToolbarColor("#609EE8")
            .setToolbarTextColor("#FFFFFF")
            .setBackgroundColor("#F2F2F2")
            .setShowNumberIndicator(true)
            .setMaxSize(3)
            .setLimitMessage(getString(R.string.photo_message))
            .setRequestCode(100)
            .start()
    }

    private fun checkInputFields(): Boolean {
        poster.userUid = userModel.uid
        poster.userName = userModel.name
        poster.title = titleCreatePoster.text.toString()
        poster.description = descriptionCreatePoster.text.toString()
        poster.dateStart = getCurrentTime().toString()
        if(checkBoxIsPhone.isChecked){
            poster.isPhone = true
            poster.userPhone = userModel.phone
        }
        if(poster.title.isEmpty()){
            errorTextViewCreate.text = getString(R.string.not_found_title)
            return false
        }
        if(poster.description.isEmpty()){
            errorTextViewCreate.text = getString(R.string.not_found_description)
            return false
        }
        if(poster.address.isEmpty() || poster.latitude == 0.0 || poster.longitude == 0.0){
            errorTextViewCreate.text = getString(R.string.not_found_location)
            return false
        }
        if(poster.dateStart.isEmpty()){
            errorTextViewCreate.text = getString(R.string.not_found_date)
            return false
        }

        return true
    }

    fun successAdd(){
        hideProgressBar()
        addPosterButton.isClickable = true
        addPosterButton.isEnabled = true
        context?.showToast(getString(R.string.success_add_poster))
    }
    fun canceledAdd(){
        addPosterButton.isClickable = true
        addPosterButton.isEnabled = true
        hideProgressBar()
        context?.showToast(getString(R.string.canceled))
    }

    private fun showProgressBar(){
        createPosterProgressBar.visibility = View.VISIBLE
    }
    private fun hideProgressBar(){
        createPosterProgressBar.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandleResult(requestCode, resultCode, data, 100)) {
            val  listImages = ImagePicker.getImages(data)
            val listImageViews : ArrayList<RoundedImageView> = ArrayList()
            listImageViews.add(imageLeftItemPoster)
            listImageViews.add(imageCenterItemPoster)
            listImageViews.add(imageRightItemPoster)
            for ((i, image) in listImages.withIndex()){
                imagesUri.add(image.uri)
                Glide.with(this).load(image.uri).into(listImageViews[i])
                listImageViews[i].scaleType = ImageView.ScaleType.CENTER_CROP
            }
            if(listImages.size == 1){
                poster.imageLeft = poster.uuid + left
                poster.imageCenter = ""
                poster.imageRight = ""
            }
            if(listImages.size == 2){
                poster.imageLeft = poster.uuid + left
                poster.imageCenter = poster.uuid + center
                poster.imageRight = ""
            }
            if(listImages.size == 3){
                poster.imageLeft = poster.uuid + left
                poster.imageCenter = poster.uuid + center
                poster.imageRight = poster.uuid + right
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun showSuccessMessage() {
        context?.showToast("Фото добавлено")
    }

    fun showFailureMessage(fileName: String) {
        context?.showToast("Ошибка при добавлении фото")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == CAMERA_PICK_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED ){
                showImagePicker()
            }
        }
    }
}