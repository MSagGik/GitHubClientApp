package com.msaggik.githubclientapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.gson.Gson
import com.msaggik.githubclientapp.R
import com.msaggik.githubclientapp.di.App
import com.msaggik.githubclientapp.model.entities.oauth.Token
import com.msaggik.githubclientapp.model.network.RestGitHub
import com.msaggik.githubclientapp.model.network.RestGitHubModule
import com.msaggik.githubclientapp.view.adapter.LanguageAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val OAUTH_PREFERENCES = "oauth_preferences"
private const val TOKEN_KEY = "token_key"
class MainActivity : AppCompatActivity() {

    private lateinit var navigationController: NavController
    private lateinit var navigationHostFragment: NavHostFragment
    private lateinit var layoutSetting: FrameLayout
    private lateinit var switchTheme: SwitchMaterial
    private lateinit var fragmentContainer: FragmentContainerView
    private lateinit var languageSelection: Spinner
    private lateinit var applicationUserAgreement: TextView
    private lateinit var oauthPlaceholder: TextView
    private var isOnSetting = false
    private lateinit var sharedPreferences: SharedPreferences

    private val gitHubBaseURL = "https://github.com"
    private val retrofit = RestGitHubModule.createRetrofitObject(gitHubBaseURL)
    private val gitHubRestService = retrofit.create(RestGitHub::class.java)

    @SuppressLint("MissingInflatedId", "RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layoutSetting = findViewById(R.id.layoutSetting)
        switchTheme = findViewById(R.id.switchTheme)
        fragmentContainer = findViewById(R.id.fragmentContainerView)
        languageSelection = findViewById(R.id.language_selection)
        applicationUserAgreement = findViewById(R.id.application_user_agreement)
        oauthPlaceholder = findViewById(R.id.oauth_placeholder)
        navigationHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navigationController = navigationHostFragment.navController

        val bottomNavigationViewMenu =
            findViewById<BottomNavigationView>(R.id.bottom_navigation_menu)

        // theme settings
        switchTheme.setChecked((applicationContext as App).getApplicationTheme())

        switchTheme.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).setThemeSharedPreferences(checked)
        }

        // language settings
        val languageValues = resources.getStringArray(R.array.language_selection_values)
        val languageSelectionAdapter = LanguageAdapter(
            this,
            resources.getIntArray(R.array.language_selection),
            resources.getStringArray(R.array.language_selection)
        )
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

        // setting up application information
        applicationUserAgreement.setOnClickListener(listener)

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
                        if (fragment.javaClass.simpleName.equals("OauthItemFragment")) {
                            flag = false
                            findNavController(R.id.fragmentContainerView).navigate(R.id.oauthItemFragment)
                        }else if (fragment.javaClass.simpleName.equals("OauthSearchFragment")) {
                            flag = false
                            findNavController(R.id.fragmentContainerView).navigate(R.id.oauthSearchFragment)
                        }
                    }
                    if (flag) {
                        findNavController(R.id.fragmentContainerView).navigate(R.id.authenticationFragment)
                    }
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
                            findNavController(R.id.fragmentContainerView).navigate(R.id.itemFragment)
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

    val listener: View.OnClickListener = object : View.OnClickListener {
        override fun onClick(p0: View?) {
            when (p0?.id) {
                R.id.application_user_agreement -> {
                    val agreementUri: Uri = Uri.parse(getString(R.string.uri_application_information))
                    val agreementIntent = Intent(Intent.ACTION_VIEW, agreementUri)
                    startActivity(agreementIntent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Receiving data after authentication
        val url: Uri? = intent.data
        intent.data = null
        if(url != null) {
            val code = url.toString().substringAfter("code=")
            val clientId = getString(R.string.client_id)
            val clientSecret = getString(R.string.client_secret)
            getToken(clientId, clientSecret, code)
            placeholderOffMessage()
        }
    }

    private fun getToken(clientId: String, clientSecret: String, code: String) {

        if (!clientId.isNullOrEmpty() && !clientSecret.isNullOrEmpty() && !code.isNullOrEmpty()) {
            gitHubRestService.getAccessToken(clientId = clientId, clientSecret = clientSecret, code = code).enqueue(object :
                Callback<Token> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<Token>,
                    response: Response<Token>
                ) {
                    Log.i("response repositories", "" + response.code())
                    if (response.code() == 200) {
                        if (response.body()?.accessToken?.isNotEmpty() == true) {
                            placeholderOffMessage()
                            sharedPreferences = applicationContext.getSharedPreferences(OAUTH_PREFERENCES, MODE_PRIVATE)
                            sharedPreferences.edit().putString(TOKEN_KEY, "Bearer ${response.body()?.accessToken}").apply()
                            findNavController(R.id.fragmentContainerView).navigate(R.id.oauthSearchFragment)
                        } else {
                            placeholderOnMessage(getString(R.string.incorrect_application_settings))
                        }
                    } else if(response.code() == 403){
                        placeholderOnMessage(getString(R.string.request_limit_exceeded))
                    } else {
                        placeholderOnMessage("${getString(R.string.text_placeholder_two)}\nHTTP code ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Token>, t: Throwable) {
                    placeholderOnMessage(getString(R.string.text_placeholder_two))
                    Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_LONG).show()
                    Log.e("showRepositories", t.message.toString())
                }
            })
        } else {
            placeholderOnMessage(getString(R.string.incorrect_application_settings))
        }
    }

    private fun placeholderOnMessage(message: String) {
        oauthPlaceholder.text = message
        oauthPlaceholder.visibility = View.VISIBLE
    }

    private fun placeholderOffMessage() {
        oauthPlaceholder.visibility = View.GONE
    }

    companion object {
        const val KEY_IS_SETTING = "KEY_IS_SETTING"
        const val IS_SETTING_DEFAULT = false
    }
}