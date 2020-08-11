package com.example.tokenproject

import android.app.Application
import okhttp3.internal.Internal.instance

class TokenProject : Application() {

    var user_id = ""

    @Override
    override fun onCreate() {
        super.onCreate()

    }
}