package com.madcryk.findevent.activities

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.madcryk.findevent.R
import com.madcryk.findevent.adapters.MessagesAdapter
import com.madcryk.findevent.models.EventModel
import com.madcryk.findevent.models.UserModel
import com.madcryk.findevent.presenters.EventPresenter
import com.madcryk.findevent.showToast
import kotlinx.android.synthetic.main.activity_event.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EventActivity : AppCompatActivity() {

    private lateinit var user : UserModel
    private lateinit var eventPresenter : EventPresenter
    private lateinit var event: EventModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        val intent = intent
        val uuid = intent.getStringExtra("uuid")

        eventPresenter = EventPresenter(this)
        user = UserModel.instance
        eventPresenter.loadEvent(uuid, user.uid)
    }

    fun initViews(e: EventModel) {
        event = e
        eventPresenter.listenerCountFollowers(e.uuid)
        eventPresenter.changeCountFollowersListener(event.uuid)
        val userBitmap : Bitmap = intent.getParcelableExtra("user_image")
        imageUserEvent.setImageBitmap(userBitmap)
        titleEvent.text = event.title
        timeCreateEvent.text = event.timeCreate
        nameUserEvent.text = event.userName
        addressEvent.text = event.address
        if(event.isPhone){
            isPhoneItemEvent.visibility = View.VISIBLE
            phoneUserEvent.text = event.userPhone
        }
        descriptionEvent.text = event.body
        if((event.maxPersons - event.count) <= 0){
            enablePersonsEvent.text = getString(R.string.unenabled_person)
            addUserInEventButton.isEnabled = false
            addUserInEventButton.isClickable = false
        }else{
            enablePersonsEvent.text = (event.maxPersons - event.count).toString()
        }
        timeStartEvent.text = event.time
        addUserInEventButton.setOnClickListener {
            eventPresenter.addUserInEvent(event.uuid, user.uid, ++(event.count))
        }
        discussionsEventButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("uuid", event.uuid)
            intent.putExtra("title", event.title)
            startActivity(intent)
        }
    }

    fun checkEventFollowing(isFollow: Boolean) {
        if (isFollow) {
            addUserInEventButton.isClickable = false
            addUserInEventButton.isEnabled = false
            discussionsEventButton.visibility = View.VISIBLE
        } else {
            addUserInEventButton.isClickable = true
            addUserInEventButton.isEnabled = true
            discussionsEventButton.visibility = View.GONE
        }
    }

    fun showProgressBar(){
        eventContainer.visibility = View.INVISIBLE
        loadEventProgressBar.visibility = View.VISIBLE
    }
    fun hideProgressBar(){
        eventContainer.visibility = View.VISIBLE
        loadEventProgressBar.visibility = View.INVISIBLE
    }

    fun successAddUser() {
        discussionsEventButton.visibility = View.VISIBLE
        showToast(getString(R.string.success_follow))
    }

    fun failureAddUser() {
        showToast(getString(R.string.error_follow))
    }

    fun updateCountFollowers(count : Long) {
        if((event.maxPersons - count) <= 0){
            enablePersonsEvent.text = getString(R.string.unenabled_person)
            addUserInEventButton.isEnabled = false
            addUserInEventButton.isClickable = false
        }else{
            enablePersonsEvent.text = (event.maxPersons - count).toString()
        }
    }

    fun setCountFollowers(i: Long) {
        enablePersonsEvent.text = (event.maxPersons - i.toInt()).toString()
        event.count = i.toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventPresenter.disconnect()
    }
}