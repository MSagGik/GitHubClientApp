package com.msaggik.githubclientapp.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.msaggik.githubclientapp.R

class StartActivity : AppCompatActivity(), Runnable {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        Thread(this).start()
    }

    override fun run() {
        Thread.sleep(1000)
        startActivity(Intent(applicationContext, MainActivity::class.java))
    }
}