package com.techmoda.view.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.techmoda.ProviderType
import com.techmoda.R

class HomeActivity : AppCompatActivity() {

    private lateinit var emailLbl: TextView
    private lateinit var providerLbl: TextView
    private lateinit var logOutBtn: Button

    private var provider : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        emailLbl = findViewById(R.id.emailLbl)
        providerLbl = findViewById(R.id.providerLbl)
        logOutBtn = findViewById(R.id.logOutBtn)

        val email = intent.extras?.getString("email")
        provider = intent.extras?.getString("provider")

        emailLbl.text = "Email: " + email
        providerLbl.text = "register method: " + provider

        setup()

        //Guardado de datos y Credenciales
        val prefs = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }

    private fun setup(){
        //Log out
        logOutBtn.setOnClickListener {
            val prefs = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            if (provider == ProviderType.FACEBOOK.name){
                LoginManager.getInstance().logOut()
            }

            FirebaseAuth.getInstance().signOut()
            showLogin()
        }
    }

    //Start Login activity
    private fun showLogin(){
        val loginIntent = Intent(this, LoginActivity::class.java).apply { } //Edit for new
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(loginIntent)
    }

}