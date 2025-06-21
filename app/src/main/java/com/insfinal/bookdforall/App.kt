package com.insfinal.bookdforall

import android.app.Application
import com.insfinal.bookdforall.session.SessionManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(applicationContext)
    }
}