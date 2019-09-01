package com.viictrp.financeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {

    private val splashTime = 3000L // 3 Segundos
    private lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mHandler = Handler()
        mostrarMainActitivy()
    }

    private fun mostrarMainActitivy() {
        mHandler.postDelayed({
            val intent = Intent(
                this@SplashActivity,
                MainActivity::class.java
            )
            startActivity(intent)
            finish()
        }, splashTime)
    }
}
