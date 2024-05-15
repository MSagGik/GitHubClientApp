package com.msaggik.githubclientapp.view

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.msaggik.githubclientapp.R

class MainActivity : AppCompatActivity() {

    private lateinit var navigationController: NavController
    private lateinit var navigationHostFragment: NavHostFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navigationController = navigationHostFragment.navController

        val bottomNavigationViewMenu =
            findViewById<BottomNavigationView>(R.id.bottom_navigation_menu)

        // bottom navigation bar
        bottomNavigationViewMenu.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.profile -> {
                    navigationController.currentDestination?.id?.let {
                        findNavController(R.id.fragmentContainerView).popBackStack(
                            it, true
                        )
                    }
                    var flag = true
                    for (fragment in navigationHostFragment.childFragmentManager.fragments) {
                        if (fragment.javaClass.simpleName.equals("ProfileFragment")) {
                            flag = false
                            findNavController(R.id.fragmentContainerView).navigate(R.id.profileFragment)
                        }
                    }
                    if(flag) findNavController(R.id.fragmentContainerView).navigate(R.id.authenticationFragment)
                    true
                }
                R.id.search -> {
                    navigationController.currentDestination?.id?.let {
                        findNavController(R.id.fragmentContainerView).popBackStack(
                            it, true
                        )
                    }
                    var flag = true
                    for (fragment in navigationHostFragment.childFragmentManager.fragments) {
                        if (fragment.javaClass.simpleName.equals("UserFragment")) {
                            flag = false
                            findNavController(R.id.fragmentContainerView).navigate(R.id.userFragment)
                        }
                    }
                    if(flag) findNavController(R.id.fragmentContainerView).navigate(R.id.searchFragment)
                    true
                }
                R.id.settings -> {
                    navigationController.currentDestination?.id?.let {
                        findNavController(R.id.fragmentContainerView).popBackStack(
                            it, true
                        )
                    }
                    findNavController(R.id.fragmentContainerView).navigate(R.id.settingFragment)
                    true
                }
                else -> false
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        })
    }

}
