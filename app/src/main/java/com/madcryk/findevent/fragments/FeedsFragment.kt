package com.madcryk.findevent.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.madcryk.findevent.R
import kotlinx.android.synthetic.main.fragment_feeds.*


class FeedsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feeds, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        replaceCurrentFragment( EventsFragment() )
        initViews()
    }

    private fun initViews() {
        postersListButton.setOnClickListener {
            val postersFragment = PostersFragment()
            replaceCurrentFragment(postersFragment)
            postersListButton.setTextColor(context!!.getColor(R.color.blueColor))
            eventsListButton.setTextColor(context!!.getColor(R.color.textColor2))
            postersListButton.textSize = 16F
            eventsListButton.textSize = 14F
        }
        eventsListButton.setOnClickListener {
            val eventsFragment = EventsFragment()
            replaceCurrentFragment(eventsFragment)
            eventsListButton.setTextColor(context!!.getColor(R.color.blueColor))
            postersListButton.setTextColor(context!!.getColor(R.color.textColor2))
            eventsListButton.textSize = 16F
            postersListButton.textSize = 14F
        }
    }

    private fun replaceCurrentFragment(fragment : Fragment){
        requireFragmentManager()
            .beginTransaction()
//            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragmentFeedList, fragment, fragment.javaClass.simpleName)
            .commit()
    }
}