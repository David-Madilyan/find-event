package com.madcryk.findevent.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.madcryk.findevent.R
import com.madcryk.findevent.fragments.CreateFeedFragment
import com.madcryk.findevent.fragments.FeedsFragment
import com.madcryk.findevent.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        makeCurrentFragment(FeedsFragment(), getString(R.string.feeds_fragment))
    }

    private fun initViews() {
        var selectedItem : Int = 0
        navigation_menu.setOnNavigationItemSelectedListener OnNavigationItemSelectedListener@{ menuItem ->
            if(menuItem.itemId != selectedItem){
                when (menuItem.itemId) {
                    R.id.menu_item_feed -> {
                        selectedItem = R.id.menu_item_feed
                        makeCurrentFragment(FeedsFragment(), getString(R.string.feeds_fragment))
                    }
                    R.id.menu_item_profile -> {
                        selectedItem = R.id.menu_item_profile
                        makeCurrentFragment(ProfileFragment(), getString(R.string.profile_fragment))
                    }
                    R.id.menu_item_create -> {
                        selectedItem = R.id.menu_item_create
                        makeCurrentFragment(CreateFeedFragment(), getString(R.string.create_fragment))
                    }

                }
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment : Fragment, tag : String){
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .replace(R.id.fragmentHostMain, fragment, tag)
            .commit()
    }
}