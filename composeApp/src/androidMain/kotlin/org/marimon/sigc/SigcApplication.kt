package org.marimon.sigc

import android.app.Application

class SigcApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase removido temporalmente - usando almacenamiento local
    }
}