package com.streamtv.util

import android.os.Handler
import android.os.Looper

class NumberInputHandler(private val onChannelSelected: (Int) -> Unit, private val onUpdateUI: (String) -> Unit) {
    private var inputBuffer = ""
    private val handler = Handler(Looper.getMainLooper())
    private val timeoutRunnable = Runnable {
        if (inputBuffer.isNotEmpty()) {
            onChannelSelected(inputBuffer.toInt())
            inputBuffer = ""
            onUpdateUI("")
        }
    }

    fun handleInput(digit: Int) {
        if (inputBuffer.length < 4) {
            inputBuffer += digit
            onUpdateUI("CH: ${inputBuffer}_")
            handler.removeCallbacks(timeoutRunnable)
            handler.postDelayed(timeoutRunnable, 2000)
        }
    }

    fun submit() {
        if (inputBuffer.isNotEmpty()) {
            handler.removeCallbacks(timeoutRunnable)
            timeoutRunnable.run()
        }
    }

    fun cancel() {
        inputBuffer = ""
        handler.removeCallbacks(timeoutRunnable)
        onUpdateUI("")
    }
}
