package com.madcryk.findevent.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.madcryk.findevent.R
import com.madcryk.findevent.adapters.PostersAdapter
import com.madcryk.findevent.models.PosterModel
import com.madcryk.findevent.presenters.PostersPresenter
import kotlinx.android.synthetic.main.fragment_events.*
import kotlinx.android.synthetic.main.fragment_posters.*
import kotlinx.android.synthetic.main.view_search.view.*

class PostersFragment : Fragment() {
    private lateinit var postersPresenter: PostersPresenter
    private lateinit var postersAdapter: PostersAdapter
    private var listPosters: ArrayList<PosterModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
    }

    private fun initViews() {
        swipeContainerPosters.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)
        postersPresenter = PostersPresenter(this)
        loadPostersList()
        swipeContainerPosters.setOnRefreshListener {
            refreshListPosters()
        }
        postersSearchView.execute_search_button.setOnClickListener {
            if(postersSearchView.search_input_text.text.toString().isNotEmpty()){
                searchPosters(postersSearchView.search_input_text.text.toString())
            }
        }
        recyclerViewPostersScrolled()
    }

    private fun recyclerViewPostersScrolled() {
        recyclerViewPosters.removeOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
                val totalItemCount = layoutManager!!.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val endHasBeenReached : Boolean = lastVisible + 2 >= totalItemCount
                if (totalItemCount > 0 && endHasBeenReached) {
//                    showAddProgressBar()
//                    swipeContainerEvents.isEnabled = false
//                    postersPresenter.addPostersRange(
//                        start = totalItemCount.toDouble(),
//                        end = totalItemCount.toDouble() + 20
//                    )
                }
            }

        })
    }

    private fun loadPostersList() {
        swipeContainerPosters.isEnabled = false
        showProgressBar()
        postersPresenter.loadRangeListPosters()
    }

    private fun refreshListPosters(){
        postersPresenter.refreshListPosters()
    }
    private fun searchPosters(fetchString : String){
        postersAdapter.removeAllPosters()
        showProgressBar()
        swipeContainerPosters.isEnabled = false
        postersPresenter.findPostersByTitleDesc(fetchString)
    }

    fun completeSearchPosters(posters: ArrayList<PosterModel>){
        hideProgressBar()
        swipeContainerPosters.isEnabled = true
        if(posters.isNotEmpty()){
            listPosters = posters
            postersAdapter.removeAllPosters()
            postersAdapter.refreshAllPosters(posters)
        }
    }

    fun completeRefreshListPosters(posters: ArrayList<PosterModel>){
        swipeContainerPosters.isRefreshing = false
        if(posters.isNotEmpty()){
            listPosters = posters
            postersAdapter.removeAllPosters()
            postersAdapter.refreshAllPosters(listPosters)
        }
    }
    fun completeLoadPosters( posters : ArrayList<PosterModel>){
        hideProgressBar()
        swipeContainerPosters.isEnabled = true
        if(posters.isNotEmpty()){
            listPosters = posters
            postersAdapter = PostersAdapter(this.requireContext(), listPosters)
            recyclerViewPosters.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = postersAdapter
            }

        }
    }

    fun completeAddPosters(posters: ArrayList<PosterModel>){
        hideAddProgressBar()
        swipeContainerPosters.isEnabled = true
        if(posters.isNotEmpty()){
            listPosters.addAll(posters)
            postersAdapter.addNewPosters(posters)
        }
    }

    private fun showProgressBar(){
        loadProgressBarPosters.visibility = View.VISIBLE
    }
    private fun hideProgressBar(){
        loadProgressBarPosters.visibility = View.GONE
    }

    fun showAddProgressBar(){
        addProgressBarPosters.visibility = View.VISIBLE
    }
    private fun hideAddProgressBar(){
        addProgressBarPosters.visibility = View.GONE
    }
}