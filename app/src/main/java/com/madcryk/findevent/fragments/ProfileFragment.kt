package com.madcryk.findevent.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.madcryk.findevent.R
import com.madcryk.findevent.activities.EditProfileActivity
import com.madcryk.findevent.activities.SettingsActivity
import com.madcryk.findevent.adapters.EventsAdapter
import com.madcryk.findevent.adapters.PostersAdapter
import com.madcryk.findevent.impl.ItemListener
import com.madcryk.findevent.models.EventModel
import com.madcryk.findevent.models.PosterModel
import com.madcryk.findevent.models.UserModel
import com.madcryk.findevent.presenters.ProfilePresenter
import com.madcryk.findevent.showToast
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.model.Image
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_events.*
import kotlinx.android.synthetic.main.fragment_feeds.*
import kotlinx.android.synthetic.main.fragment_posters.*
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment(), ItemListener {

    private val CAMERA_PICK_CODE = 0
    private lateinit var profilePresenter: ProfilePresenter
    private var userModel = UserModel.instance
    private lateinit var postersAdapter: PostersAdapter
    private lateinit var eventsAdapter: EventsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        profilePresenter = ProfilePresenter(this)
        initViews()
    }

    private fun initViews() {
        setUserDataInViews()
        userImageProfile.setOnClickListener {
            userImagePick()
        }

        settingsButtonProfile.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        editButtonProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
        profilePresenter.uploadMyEvents(userModel.uid)
        myEventsButtonProfile.setOnClickListener {
            myEventsButtonProfile.setTextColor(context!!.getColor(R.color.blueColor))
            myPostersButtonProfile.setTextColor(context!!.getColor(R.color.textColor2))
            myEventsButtonProfile.textSize = 15F
            myPostersButtonProfile.textSize = 14F

            profilePresenter.uploadMyEvents(userModel.uid)

        }

        myPostersButtonProfile.setOnClickListener {
            myPostersButtonProfile.setTextColor(context!!.getColor(R.color.blueColor))
            myEventsButtonProfile.setTextColor(context!!.getColor(R.color.textColor2))
            myPostersButtonProfile.textSize = 15F
            myEventsButtonProfile.textSize = 14F

            profilePresenter.uploadMyPosters(userModel.uid)
        }
    }

    private fun setUserDataInViews() {
        profilePresenter.loadUserImage(userUid = userModel.uid)

        userNameProfile.text = userModel.name
        userEmailProfile.text = userModel.email
        countEventsProfile.text = userModel.events.toString()
        countPostersProfile.text = userModel.posters.toString()
    }

    private fun userImagePick() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED){
            showImagePicker()
        }else{
            val permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions( requireActivity(), permissions, CAMERA_PICK_CODE)
        }
    }

    private fun showImagePicker() {
        ImagePicker.with(this)
            .setFolderMode(true)
            .setFolderTitle(getString(R.string.gallery))
            .setRootDirectoryName(Config.ROOT_DIR_DCIM)
            .setDirectoryName(getString(R.string.choise_images))
            .setMultipleMode(false)
            .setDoneTitle(getString(R.string.choice))
            .setStatusBarColor("#609EE8")
            .setToolbarColor("#609EE8")
            .setToolbarTextColor("#FFFFFF")
            .setBackgroundColor("#F2F2F2")
            .setShowNumberIndicator(true)
            .setMaxSize(1)
            .setLimitMessage(getString(R.string.one_photo_pick))
            .setRequestCode(200)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandleResult(requestCode, resultCode, data, 200)) {
            val images: ArrayList<Image> = ImagePicker.getImages(data)
            for (image in images) {
                val b = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, image.uri)
                Glide.with(this).load(image.uri).into(userImageProfile)
                profilePresenter.uploadUserImage(bitmap = b, userUid =  userModel.uid)
            }
        }
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

    fun showFailureMessage() {
        context?.showToast("Не удалось загрузить фотографию")
    }

    fun showSuccessMessage() {
        context?.showToast("Фото профиля успешно загружено")
    }

    fun completeLoadPosters(postersList: ArrayList<PosterModel>) {
        postersAdapter = PostersAdapter(this.requireContext(), postersList)
        listRecyclerViewProfile.apply {
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = postersAdapter
        }
    }

    fun completeLoadListEvents(eventsList: ArrayList<EventModel>) {
        eventsAdapter = EventsAdapter(this, eventsList)
        listRecyclerViewProfile.apply {
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = eventsAdapter
        }
    }

    override fun onClickItem(item: EventModel, bitmap: Bitmap) {

    }
}