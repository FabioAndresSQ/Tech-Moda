package com.techmoda.view.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.techmoda.MainActivity
import com.techmoda.ProviderType
import com.techmoda.R
import java.lang.Exception

class LoginActivityReserva : AppCompatActivity() {

    private lateinit var txtEmailLogin : EditText
    private lateinit var txtPasswordLogin : EditText
    private lateinit var loginBtn : Button
    private lateinit var goToRegistrarBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_reserva)

        txtEmailLogin = findViewById(R.id.txtEmailLogin)
        txtPasswordLogin = findViewById(R.id.txtPasswordLogin)
        loginBtn = findViewById(R.id.loginBtn)
        goToRegistrarBtn = findViewById(R.id.goToRegistrarBtn)

        //Analytics Events
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Login Screen displayed")
        analytics.logEvent("LoginScreen", bundle)

        //Start Components
        setup()

    }

    private fun setup(){
        loginBtn.setOnClickListener {
            if (checkEmail() && checkPassword()){ //Email and Password are correct
                //Access Firebase Auth
                try {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(txtEmailLogin.text.toString(),
                        txtPasswordLogin.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                            showHomePage(it.result?.user?.email ?: "", ProviderType.BASIC)
                        }else{
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                            showErrorAlert("Error al autenticar al Usuario")
                        }
                    }
                } catch (e : Exception){
                    Toast.makeText(this, "Couldn't authenticate. Please check Connection", Toast.LENGTH_SHORT).show()
                }
            }
        }
        goToRegistrarBtn.setOnClickListener {
            val registerIntent = Intent(this, RegistrarseActivity::class.java).apply { } //Edit for new
            startActivity(registerIntent)
        }
    }

    //Validate email input is correct
    private fun checkEmail() : Boolean {
        val email = txtEmailLogin.text.toString()
        return if ( email != ""){
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                true //Valid input
            } else{
                Toast.makeText(this, "Formato de E-mail Incorrecto", Toast.LENGTH_SHORT).show()
                txtEmailLogin.requestFocus()
                false
            }
        } else{
            Toast.makeText(this, "El E-mail esta Vacio", Toast.LENGTH_SHORT).show()
            txtEmailLogin.requestFocus()
            false
        }
    }

    //Validate password inputs are correct
    private fun checkPassword() : Boolean{
        val password = txtPasswordLogin.text.toString()
        return if (password.length > 7){
                true
        } else{
            Toast.makeText(this, "La Contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
            false
        }
    }

    //Error Alert
    private fun showErrorAlert(msg:String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(msg)
        builder.setPositiveButton("Aceptar", null)
        val dialog : AlertDialog = builder.create()
        dialog.show()
    }

    //Start HomePageActivity
    private fun showHomePage(email:String, provider: ProviderType){
        //Launch HomePage Activity
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}