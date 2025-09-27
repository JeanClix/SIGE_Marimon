package org.marimon.sigc

import android.app.Application
import com.google.firebase.FirebaseApp

class SigcApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}