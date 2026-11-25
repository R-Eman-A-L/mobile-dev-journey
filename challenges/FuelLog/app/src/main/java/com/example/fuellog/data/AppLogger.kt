package com.example.fuellog.data

import android.util.Log
import javax.inject.Inject

interface AppLogger {
    fun error(tag: String,  message: String, e: Throwable)
}

class AndroidLogger @Inject constructor(): AppLogger{
    override fun error(tag: String, message: String, e: Throwable) {
        Log.e(tag, message, e)
    }

}