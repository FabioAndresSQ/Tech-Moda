package com.techmoda

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.techmoda.view.ui.activities.LoginActivity

enum class ProviderType{
    BASIC,
    GOOGLE,
    FACEBOOK
}

private lateinit var handler: Handler

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = Handler(Looper.myLooper()!!)

        handler.postDelayed({
            val intentLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentLogin)
            finish()
        }, 1000)

    }



}