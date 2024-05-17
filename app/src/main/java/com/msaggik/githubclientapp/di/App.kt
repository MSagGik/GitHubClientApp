package com.msaggik.githubclientapp.di

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.msaggik.githubclientapp.di.repository.AppComponent
import com.msaggik.githubclientapp.di.repository.DaggerAppComponent

private const val SHARED_PREFERENCES = "shared_preferences"
private const val THEME_APP_KEY = "theme_app_key"
class App : Application() {

    lateinit var appComponent: AppComponent
    private var lightTheme = true
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create()
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        lightTheme = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE).getBoolean(THEME_APP_KEY, false)
        setApplicationTheme(lightTheme)
        Log.i("Start", "тема " + lightTheme)
    }

    fun setThemeSharedPreferences(themeEnabled: Boolean) {
        lightTheme = themeEnabled
        setApplicationTheme(lightTheme)
        sharedPreferences.edit().putBoolean(THEME_APP_KEY, lightTheme).apply()
    }

    fun getApplicationTheme(): Boolean {
        return lightTheme
    }

    private fun setApplicationTheme(themeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if(themeEnabled) {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                AppCompatDelegate.MODE_NIGHT_YES
            }
        )
    }
}