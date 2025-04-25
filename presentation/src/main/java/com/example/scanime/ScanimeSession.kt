package com.example.scanime

import android.content.Context

class ScanimeSession(private val context: Context) {

    fun isSignedIn(): Boolean {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("is_signed_in", false)
    }

}