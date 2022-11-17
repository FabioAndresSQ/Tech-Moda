package com.techmoda.view.ui.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
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
import com.google.firebase.firestore.FirebaseFirestore
import com.techmoda.ProviderType
import com.techmoda.R
import java.lang.Exception

class LoginActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100
    private lateinit var txtEmailLogin : EditText
    private lateinit var txtPasswordLogin : EditText
    private lateinit var loginBtn : Button
    private lateinit var goToRegistrarBtn : Button
    private lateinit var loginLayout : ScrollView
    private lateinit var googleLoginBtn : ImageButton
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        txtEmailLogin = findViewById(R.id.txtEmailLogin)
        txtPasswordLogin = findViewById(R.id.txtPasswordLogin)
        loginBtn = findViewById(R.id.loginBtn)
        goToRegistrarBtn = findViewById(R.id.goToRegistrarBtn)
        loginLayout = findViewById(R.id.loginLayout)
        googleLoginBtn = findViewById(R.id.googleLoginBtn)

        //Analytics Events
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message", "Login Screen displayed")
        analytics.logEvent("LoginScreen", bundle)

        //Start Components
        setup()
        sesion()
    }

    override fun onStart() {
        super.onStart()
        loginLayout.visibility = View.VISIBLE
    }

    private fun sesion(){

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val savedEmail = prefs.getString("email", null)
        val savedProvider = prefs.getString("provider", null)
        val savedImage = prefs.getString("image", null)

        if (savedEmail != null && savedProvider != null){
            loginLayout.visibility = View.INVISIBLE
            showHomePage(savedEmail, ProviderType.valueOf(savedProvider), savedImage)
        }

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
                            db.collection("users").document(it.result?.user?.email ?: "").set(
                                hashMapOf("provider" to ProviderType.BASIC.name,
                                    "nombre" to "")
                            )
                            showHomePage(it.result?.user?.email ?: "", ProviderType.BASIC, "default")
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
            val task:Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credential: AuthCredential =
                        GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show()
                                val imageUrl = it.result?.user?.photoUrl.toString()
                                db.collection("users").document(it.result?.user?.email ?: "").set(
                                    hashMapOf("provider" to ProviderType.GOOGLE.name,
                                    "nombre" to it.result.user?.displayName.toString())
                                )
                                showHomePage(it.result?.user?.email ?: "", ProviderType.GOOGLE, imageUrl)
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
    private fun showHomePage(email:String, provider: ProviderType, imageUrl:String?){
        //Launch HomePage Activity
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
            putExtra("image", imageUrl)
        }
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(homeIntent)
    }

}