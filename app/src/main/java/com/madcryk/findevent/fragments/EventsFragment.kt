package com.madcryk.findevent.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.metalab.asyncawait.async
import com.madcryk.findevent.activities.EventActivity
import com.madcryk.findevent.R
import com.madcryk.findevent.adapters.EventsAdapter
import com.madcryk.findevent.impl.ItemListener
import com.madcryk.findevent.models.EventModel
import com.madcryk.findevent.presenters.EventsPresenter
import com.madcryk.findevent.showToast
import kotlinx.android.synthetic.main.fragment_events.*
import kotlinx.android.synthetic.main.view_search.view.*


class EventsFragment : Fragment(), ItemListener {

    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var eventsPresenter : EventsPresenter
    private var listEvents : ArrayList<EventModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        eventsPresenter = EventsPresenter(this)
        initViews()
    }

    private fun initViews() {
        loadListEvents()
        recyclerViewEventsScrolled()
        swipeContainerEvents.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)
        swipeContainerEvents.setOnRefreshListener {
            refreshEvents()
        }
        eventsSearchView.execute_search_button.setOnClickListener {
            if(eventsSearchView.search_input_text.text.toString().isNotEmpty()){
                executeSearchEvents(eventsSearchView.search_input_text.text.toString())
            }
        }
    }

    private fun recyclerViewEventsScrolled() {
        recyclerViewEvents.removeOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                val totalItemCount = layoutManager!!.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val endHasBeenReached : Boolean = lastVisible + 2 >= totalItemCount
                if (totalItemCount > 0 && endHasBeenReached) {
//                    showAddProgressBar()
//                    swipeContainerEvents.isEnabled = false
//                    eventsPresenter.addEventsRange(
//                        start = totalItemCount.toDouble(),
//                        end = totalItemCount.toDouble() + 20
//                    )
                }
            }
        })
    }

    private fun executeSearchEvents(fetchString: String) {
        eventsAdapter.clearAllEvents()
        showProgressBar()
        swipeContainerEvents.isEnabled = false
        eventsPresenter.fetchEventsByTitleOrDesc(fetchString)
    }

    private fun loadListEvents() {
        async {
            showProgressBar()
            swipeContainerEvents.isEnabled = false
        }
        eventsPresenter.loadEventsRange(0, 20)
    }

    private fun refreshEvents() {
        eventsPresenter.refreshListEvents()
    }

    fun completeLoadListEvents(events : ArrayList<EventModel>){                                     // call from EventsPresenter
        hideProgressBar()
        hideAddProgressBar()
        swipeContainerEvents.isEnabled = true
        if(!events.isNullOrEmpty()){
            listEvents.addAll(events)
            eventsAdapter = EventsAdapter(this, listEvents)
            recyclerViewEvents.apply {
                layoutManager = LinearLayoutManager(
                    activity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                adapter = eventsAdapter
            }
        }
    }

    fun completeAddEvents( events : ArrayList<EventModel> ){                                        // call from EventsPresenter
        hideAddProgressBar()
        swipeContainerEvents.isEnabled = true
        if( !events.isNullOrEmpty() ){
            listEvents.addAll(events)
            eventsAdapter.addNewEvents(listEvents)
        }
    }

    fun completeSearchEvents(events: ArrayList<EventModel>){                                        // call from EventsPresenter
        hideProgressBar()
        swipeContainerEvents.isEnabled = true
        if(!events.isNullOrEmpty()){
            listEvents = events
            eventsAdapter.refreshAllEvents(listEvents)
        } else {
            context?.showToast(getString(R.string.not_found_records))
        }
    }

    fun completeRefreshListEvents(events : ArrayList<EventModel>){                                  // call from EventsPresenter
        swipeContainerEvents.isRefreshing = false
        if(!events.isNullOrEmpty()){
            listEvents = events
            eventsAdapter.refreshAllEvents(events)
        }
    }

    private fun showProgressBar(){
        loadProgressBarEvents.visibility = View.VISIBLE
    }
    private fun hideProgressBar(){
        loadProgressBarEvents.visibility = View.GONE
    }

    fun showAddProgressBar(){
        addProgressBarEvents.visibility = View.VISIBLE
    }
    private fun hideAddProgressBar(){
        addProgressBarEvents.visibility = View.GONE
    }

    override fun onClickItem(item: EventModel, bitmap: Bitmap) {
        if(item.uuid.isNotEmpty()){
            val intent = Intent(context, EventActivity::class.java)
            intent.putExtra("uuid", item.uuid)
            intent.putExtra("user_image", bitmap)
            context?.startActivity(intent)
        }
    }
}