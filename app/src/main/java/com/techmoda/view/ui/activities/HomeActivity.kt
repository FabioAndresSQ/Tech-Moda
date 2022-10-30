package com.techmoda.view.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.techmoda.R

class HomeActivity : AppCompatActivity() {

    private lateinit var emailLbl: TextView
    private lateinit var providerLbl: TextView
    private lateinit var logOutBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        emailLbl = findViewById(R.id.emailLbl)
        providerLbl = findViewById(R.id.providerLbl)
        logOutBtn = findViewById(R.id.logOutBtn)

        val email = intent.extras?.get("email")
        val provider = intent.extras?.get("provider")

        emailLbl.text = "Email: " + email.toString()
        providerLbl.text = "register method: " + provider.toString()

        logOutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            showLogin()
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()

    }

    //Start Login activity
    private fun showLogin(){
        val loginIntent = Intent(this, LoginActivityReserva::class.java).apply { } //Edit for new
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(loginIntent)
    }

}