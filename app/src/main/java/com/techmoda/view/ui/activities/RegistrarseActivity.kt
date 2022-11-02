package com.techmoda.view.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.techmoda.ProviderType
import com.techmoda.R
import java.lang.Exception

class RegistrarseActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100
    private lateinit var txtEmail : EditText
    private lateinit var txtPassword : EditText
    private lateinit var txtConfirmPassword : EditText
    private lateinit var registrarBtn : Button
    private lateinit var goToLoginBtn : Button
    private lateinit var googleLoginBtn : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrarse)

        //Assign views by id
        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtPassword)
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword)
        registrarBtn = findViewById(R.id.registrarseBtn)
        goToLoginBtn = findViewById(R.id.goToLoginBtn)
        googleLoginBtn = findViewById(R.id.googleLoginBtn)

        //Analytics Events
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Integracion de Firebase completa")
        analytics.logEvent("RegisterScreen", bundle)

        //Setup Logic
        setup()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        showLogin()
    }

    private fun setup(){
        registrarBtn.setOnClickListener {
            if (checkEmail() && checkPassword()){ //Email and Password are correct
                //Access Firebase Auth
                try {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(txtEmail.text.toString(),
                    txtPassword.text.toString()).addOnCompleteListener {

                        if (it.isSuccessful){
                            Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                            showHomePage(it.result?.user?.email ?: "", ProviderType.BASIC)
                        }else{
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                            showErrorAlert("There was an internal Error while registration")
                        }
                    }
                } catch (e : Exception){
                    Toast.makeText(this, "Couldn't authenticate. Please check Connection", Toast.LENGTH_SHORT).show()
                }
            }
        }

        goToLoginBtn.setOnClickListener {
            showLogin()
        }

        googleLoginBtn.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build()

            val googleClient : GoogleSignInClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential: AuthCredential =
                        GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                                showHomePage(it.result?.user?.email ?: "", ProviderType.GOOGLE)
                            } else {
                                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                                showErrorAlert("Error al autenticar con Google")
                            }
                        }
                }
            } catch (e : ApiException){
                showErrorAlert("Error al autenticar con Google")
            }
        }
    }

    //Validate email input is correct
    private fun checkEmail() : Boolean {
        val email = txtEmail.text.toString()
        return if ( email != ""){
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                true //Valid input
            } else{
                Toast.makeText(this, "Formato de E-mail Incorrecto", Toast.LENGTH_SHORT).show()
                txtEmail.requestFocus()
                false
            }
        } else{
            Toast.makeText(this, "El E-mail esta Vacio", Toast.LENGTH_SHORT).show()
            txtEmail.requestFocus()
            false
        }
    }

    //Validate password inputs are correct
    private fun checkPassword() : Boolean{
        val password = txtPassword.text.toString()
        val confirmPassword = txtConfirmPassword.text.toString()
        return if (password.length > 7){
            if(password == confirmPassword){
                true
            } else{
                Toast.makeText(this, "Las Contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                false
            }
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
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(homeIntent)
    }

    //Start Login activity
    private fun showLogin(){
        val loginIntent = Intent(this, LoginActivity::class.java).apply { } //Edit for new
        startActivity(loginIntent)
    }
}