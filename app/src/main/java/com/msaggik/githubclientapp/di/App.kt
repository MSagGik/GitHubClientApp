package com.msaggik.githubclientapp.di

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.msaggik.githubclientapp.R
import com.msaggik.githubclientapp.di.repository.AppComponent
import com.msaggik.githubclientapp.di.repository.DaggerAppComponent

private const val SHARED_PREFERENCES = "shared_preferences"
private const val THEME_APP_KEY = "theme_app_key"
private const val LANGUAGE_APP_KEY = "language_app_key"
class App : Application() {

    lateinit var appComponent: AppComponent
    private var lightTheme = true
    private var language: String = ""
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create()
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        lightTheme = sharedPreferences.getBoolean(THEME_APP_KEY, false)
        setApplicationTheme(lightTheme)
        language = sharedPreferences.getString(LANGUAGE_APP_KEY, getString(R.string.default_)).toString()
        setApplicationLanguage(language)
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

    fun setLanguageSharedPreferences(languageAdd: String) {
        language = languageAdd
        sharedPreferences.edit().putString(LANGUAGE_APP_KEY, language).apply()
        setApplicationLanguage(language)
    }

    fun getApplicationLanguage(): String {
        return language
    }

    private fun setApplicationLanguage(language: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
}