package com.mvi.features

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.mvi.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        with(NavHostFragment.create(R.navigation.main_navigation)) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, this)
                .setPrimaryNavigationFragment(this)
                .commit()
        }
    }
}
