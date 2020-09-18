package com.madcryk.findevent.presenters

import android.util.Log
import co.metalab.asyncawait.async
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.madcryk.findevent.fragments.EventsFragment
import com.madcryk.findevent.models.EventModel
import com.madcryk.findevent.models.UserModel

class EventsPresenter(_view: EventsFragment) {
    private var view = _view
    private var dbRef: DatabaseReference = Firebase.database.reference
    private var eventsList : ArrayList<EventModel> = ArrayList()
    private var userModel = UserModel.instance
    fun loadEventsRange(start: Int, end: Int) {
        eventsList = ArrayList()
        async {
            val eventsQuery = dbRef.child("events_list").limitToFirst(1000)
            eventsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.w("response", error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue<EventModel>() as EventModel
                        if(checkLocation(item.latitude, item.longitude))
                        {
                            eventsList.add(item)
                        }
                    }
                    view.completeLoadListEvents(eventsList)
                }

            })
        }
    }

    private fun checkLocation(lat: Double, lon: Double): Boolean {
        return (rangeCoord(userModel.latitude, lat) && rangeCoord(userModel.longitude, lon))
    }

    private fun rangeCoord(uc: Double, ec: Double): Boolean {
        return uc < ec + 0.25 && uc > ec - 0.25
    }


    fun refreshListEvents() {
        val eventsQuery = dbRef.child("events_list").limitToFirst(1000)
        eventsList = ArrayList()
        async {
            eventsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.w("response", error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue<EventModel>() as EventModel
                        if(checkLocation(item.latitude, item.longitude))
                        {
                            eventsList.add(item)
                        }
                    }
                    view.completeRefreshListEvents(eventsList)
                }
            })
        }
    }

    fun fetchEventsByTitleOrDesc(s: String) {
        val titleFetchQuery = dbRef.child("events_list")
            .orderByChild("title").equalTo(s)
        eventsList = ArrayList()
        async {
            titleFetchQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Log.w("response", error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue<EventModel>() as EventModel
                        if(checkLocation(item.latitude, item.longitude))
                        {
                            eventsList.add(item)
                        }
                    }
                    view.completeSearchEvents(eventsList)
                }
            })
        }
    }

    fun addEventsRange(start: Double, end: Double) {
        val eventsQuery = dbRef.child("events_list").startAt(start).endAt(end)
        eventsList = ArrayList()
        async {
            await {
            }
            view.completeAddEvents(eventsList)
        }
    }
}