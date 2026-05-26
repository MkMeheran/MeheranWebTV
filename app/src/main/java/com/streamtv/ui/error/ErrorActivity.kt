package com.streamtv.ui.error

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.streamtv.ui.splash.SplashActivity

class ErrorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resources.getIdentifier("activity_error", "layout", packageName))

        val btnRetry = findViewById<Button>(resources.getIdentifier("btnRetry", "id", packageName))
        val btnExit = findViewById<Button>(resources.getIdentifier("btnExit", "id", packageName))

        btnRetry.setOnClickListener {
            startActivity(Intent(this, SplashActivity::class.java))
            finish()
        }
        btnExit.setOnClickListener {
            finishAffinity()
        }
    }
}
