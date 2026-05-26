package com.streamtv.data

import android.content.Context
import android.content.SharedPreferences

class CacheManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("streamtv_cache", Context.MODE_PRIVATE)

    fun saveChannels(channelsJson: String) {
        prefs.edit().putString("channels", channelsJson).apply()
    }

    fun getChannels(): String? {
        return prefs.getString("channels", null)
    }

    fun saveConfig(configJson: String) {
        prefs.edit().putString("config", configJson).apply()
    }

    fun getConfig(): String? {
        return prefs.getString("config", null)
    }
}
