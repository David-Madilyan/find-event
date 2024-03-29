package com.madcryk.findevent.customViews

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import android.widget.Toast
import com.madcryk.findevent.R
import com.madcryk.findevent.hideKeyboard

import kotlinx.android.synthetic.main.view_search.view.*

class SearchView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_search, this, true)

        open_search_button.setOnClickListener { openSearch() }
        close_search_button.setOnClickListener { closeSearch() }
//        execute_search_button.setOnClickListener { executeSearch() }
    }

    private fun executeSearch() {
        Toast.makeText(context, search_input_text.text.toString(), Toast.LENGTH_SHORT).show()
        context.hideKeyboard(this)
    }

    private fun openSearch() {
        search_input_text.setText("")
        search_open_view.visibility = View.VISIBLE   // RelativeLayout visible
        val circularReveal = ViewAnimationUtils.createCircularReveal(
            search_open_view,
            (open_search_button.right + open_search_button.left) / 2,
            (open_search_button.top + open_search_button.bottom) / 2,
            0f, width.toFloat()
        )
        circularReveal.duration = 300
        circularReveal.start()
        circularReveal.addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) = Unit
            override fun onAnimationCancel(animation: Animator?) = Unit
            override fun onAnimationStart(animation: Animator?) = Unit
            override fun onAnimationEnd(animation: Animator?){
                open_search_button.visibility = View.GONE
            }
        })

    }

    private fun closeSearch() {
        val circularConceal = ViewAnimationUtils.createCircularReveal(
            search_open_view,
            (open_search_button.right + open_search_button.left) / 2,
            (open_search_button.top + open_search_button.bottom) / 2,
            width.toFloat(), 0f
        )

        circularConceal.duration = 300
        circularConceal.start()
        circularConceal.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) = Unit
            override fun onAnimationCancel(animation: Animator?) = Unit
            override fun onAnimationStart(animation: Animator?) = Unit
            override fun onAnimationEnd(animation: Animator?) {
                search_open_view.visibility = View.INVISIBLE
                search_input_text.setText("")
                circularConceal.removeAllListeners()
                open_search_button.visibility = View.VISIBLE

            }
        })
        context.hideKeyboard(this)
    }

}