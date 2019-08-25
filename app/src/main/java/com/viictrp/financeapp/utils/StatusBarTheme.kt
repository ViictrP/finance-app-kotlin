package com.viictrp.financeapp.utils

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.os.Build
import androidx.core.content.ContextCompat

class StatusBarTheme {

    companion object StatusBar {

        fun setLightStatusBar(view: View?, activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (view != null) {
                    var flags = view.systemUiVisibility
                    flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    view.systemUiVisibility = flags
                    activity.window.statusBarColor = Color.WHITE
                }
            }
        }

        fun clearLightStatusBar(activity: Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val window = activity.window
                window.statusBarColor = ContextCompat
                    .getColor(activity, android.R.color.background_light)
            }
        }
    }
}