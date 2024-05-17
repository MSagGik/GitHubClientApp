package com.msaggik.githubclientapp.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.msaggik.githubclientapp.R
import com.msaggik.githubclientapp.di.App
import com.msaggik.githubclientapp.view.adapter.LanguageAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var navigationController: NavController
    private lateinit var navigationHostFragment: NavHostFragment
    private lateinit var layoutSetting: FrameLayout
    private lateinit var switchTheme: SwitchMaterial
    private lateinit var fragmentContainer: FragmentContainerView
    private lateinit var languageSelection: Spinner
    private var isOnSetting = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layoutSetting = findViewById(R.id.layoutSetting)
        switchTheme = findViewById(R.id.switchTheme)
        fragmentContainer = findViewById(R.id.fragmentContainerView)
        languageSelection = findViewById(R.id.language_selection)
        navigationHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navigationController = navigationHostFragment.navController

        val bottomNavigationViewMenu =
            findViewById<BottomNavigationView>(R.id.bottom_navigation_menu)

        // настройки темы
        switchTheme.setChecked((applicationContext as App).getApplicationTheme())

        switchTheme.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).setThemeSharedPreferences(checked)
        }

        // настройки языка
        val languageValues = resources.getStringArray(R.array.language_selection_values)
        val languageSelectionAdapter = LanguageAdapter(this,
            resources.getIntArray(R.array.language_selection),
            resources.getStringArray(R.array.language_selection))
        languageSelection.adapter = languageSelectionAdapter

        languageSelection.setSelection(languageValues.indexOf((applicationContext as App).getApplicationLanguage()))

        languageSelection.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (applicationContext as App).setLanguageSharedPreferences(languageValues[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }


        // bottom navigation bar
        bottomNavigationViewMenu.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.profile -> {
                    isOnSetting = false
                    visibleSetting(isOnSetting)
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
                    if (flag) findNavController(R.id.fragmentContainerView).navigate(R.id.authenticationFragment)
                    true
                }

                R.id.search -> {
                    isOnSetting = false
                    visibleSetting(isOnSetting)
                    navigationController.currentDestination?.id?.let {
                        findNavController(R.id.fragmentContainerView).popBackStack(
                            it, true
                        )
                    }
                    var flag = true
                    for (fragment in navigationHostFragment.childFragmentManager.fragments) {
                        if (fragment.javaClass.simpleName.equals("ItemFragment")) {
                            flag = false
                            findNavController(R.id.fragmentContainerView).navigate(R.id.userFragment)
                        }
                    }
                    if (flag) findNavController(R.id.fragmentContainerView).navigate(R.id.searchFragment)
                    true
                }

                R.id.settings -> {
                    isOnSetting = true
                    visibleSetting(isOnSetting)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_IS_SETTING, isOnSetting)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isOnSetting = savedInstanceState.getBoolean(KEY_IS_SETTING, IS_SETTING_DEFAULT)
        visibleSetting(isOnSetting)
    }

    private fun visibleSetting(checked: Boolean) {
        if (checked) {
            layoutSetting.visibility = View.VISIBLE
            fragmentContainer.visibility = View.GONE
        } else {
            layoutSetting.visibility = View.GONE
            fragmentContainer.visibility = View.VISIBLE
        }
    }

    companion object {
        const val KEY_IS_SETTING = "KEY_IS_SETTING"
        const val IS_SETTING_DEFAULT = false
    }
}
