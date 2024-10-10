package com.example.kkbimage

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    companion object{
        val TAG: String = "kkbank"
    }

    // Reference: https://developer.android.com/training/dependency-injection/hilt-android?hl=ko

}