package com.streamtv.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.streamtv.ui.channellist.ChannelListActivity
import com.streamtv.ui.error.ErrorActivity
import com.streamtv.util.NetworkUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resources.getIdentifier("activity_splash", "layout", packageName))

        lifecycleScope.launch {
            delay(1000)
            if (NetworkUtil.isConnected(this@SplashActivity)) {
                startActivity(Intent(this@SplashActivity, ChannelListActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, ErrorActivity::class.java))
            }
            finish()
        }
    }
}
