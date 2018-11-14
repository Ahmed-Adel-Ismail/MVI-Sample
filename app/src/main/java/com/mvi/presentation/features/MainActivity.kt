package com.mvi.presentation.features

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import com.mvi.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        with(supportFragmentManager to NavHostFragment.create(R.navigation.main_navigation)) {
            first.registerFragmentLifecycleCallbacks(retainInstances(), true)
            first.beginTransaction()
                .replace(R.id.fragment_container, second)
                .setPrimaryNavigationFragment(second)
                .commit()
        }
    }
}

fun retainInstances() = object : FragmentManager.FragmentLifecycleCallbacks() {
    override fun onFragmentCreated(fragmentManager: FragmentManager, fragment: Fragment, savedInstanceState: Bundle?) {
        fragment.retainInstance = true
    }
}

