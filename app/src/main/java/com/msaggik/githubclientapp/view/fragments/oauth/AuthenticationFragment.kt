package com.msaggik.githubclientapp.view.fragments.oauth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.msaggik.githubclientapp.R

class AuthenticationFragment : Fragment() {

    private lateinit var context: Context

    private lateinit var button: Button

    private val gitHubBaseUrlOauth = "https://github.com/login/oauth/authorize"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_authentication, container, false)

        context = view.context

        button = view.findViewById(R.id.button_login_in)

        button.setOnClickListener(listener)

        return view
    }

    private val listener: View.OnClickListener = object : View.OnClickListener {
        @SuppressLint("NotifyDataSetChanged")
        override fun onClick(p0: View?) {
            when (p0?.id) {
                R.id.button_login_in -> {
                    val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("${gitHubBaseUrlOauth}?client_id=${getString(R.string.client_id)}&scope=repo&redirect_url=${getString(R.string.redirect_url)}"))
                    startActivity(intent)
                }
            }
        }
    }
}