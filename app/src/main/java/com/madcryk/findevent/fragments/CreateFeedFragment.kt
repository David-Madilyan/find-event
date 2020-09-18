package com.madcryk.findevent.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.madcryk.findevent.R
import kotlinx.android.synthetic.main.dialog_hint_create.view.*
import kotlinx.android.synthetic.main.fragment_create_feed.*
import kotlinx.android.synthetic.main.fragment_feeds.*

class CreateFeedFragment : Fragment() {
    private lateinit var sp : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sp = requireContext().getSharedPreferences("create_feed_fragment", Context.MODE_PRIVATE)
        initViews()
    }

    private fun initViews() {
        val isFirstOpen = sp.getBoolean("first_open", true)
        if(isFirstOpen){
            visibleHintDialog()
        }else {
            replaceCurrentFragment(CreateEventFragment())
        }
        eventButtonCreate.setOnClickListener {
            val fragment = CreateEventFragment()
            replaceCurrentFragment(fragment)
            eventButtonCreate.setTextColor(context!!.getColor(R.color.blueColor))
            posterButtonCreate.setTextColor(context!!.getColor(R.color.textColor2))
            eventButtonCreate.textSize = 16F
            posterButtonCreate.textSize = 14F
        }
        posterButtonCreate.setOnClickListener {
            val fragment = CreatePosterFragment()
            replaceCurrentFragment(fragment)
            posterButtonCreate.setTextColor(context!!.getColor(R.color.blueColor))
            eventButtonCreate.setTextColor(context!!.getColor(R.color.textColor2))
            posterButtonCreate.textSize = 16F
            eventButtonCreate.textSize = 14F
        }
    }

    private fun replaceCurrentFragment(fragment : Fragment){
        requireFragmentManager()
            .beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragmentHostCreate, fragment, fragment.javaClass.simpleName)
            .commit()
    }

    private fun visibleHintDialog() {
        sp.edit().putBoolean("first_open", false).apply()
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_hint_create, null)
        val mBuilder = AlertDialog
            .Builder(requireContext(), R.style.hint_dialog_style)
            .setView(mDialogView)
            .show()

        mDialogView.buttonDialogHint.setOnClickListener {
            mBuilder.dismiss()
        }
        mBuilder.setOnDismissListener {
            replaceCurrentFragment(CreateEventFragment())
        }
    }
}