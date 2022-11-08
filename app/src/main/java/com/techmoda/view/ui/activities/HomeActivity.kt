package com.techmoda.view.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.techmoda.ProviderType
import com.techmoda.R
import de.hdodenhof.circleimageview.CircleImageView

class HomeActivity : AppCompatActivity() {

    private lateinit var emailLbl: TextView
    private lateinit var imageProfile: CircleImageView
    private lateinit var homeBtn: ImageButton
    private lateinit var cartBtn: ImageButton
    private lateinit var contactBtn: ImageButton
    private lateinit var barcodeBtn: ImageButton
    lateinit var toggle : ActionBarDrawerToggle
    private var email: String? = null
    private var provider : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when (it.itemId){
                R.id.verCarrito -> Toast.makeText(this,"Carrito",Toast.LENGTH_SHORT).show()
                R.id.comentarios -> Toast.makeText(this,"Comentarios",Toast.LENGTH_SHORT).show()
                R.id.contacto -> Toast.makeText(this,"Contacto",Toast.LENGTH_SHORT).show()
                R.id.cerrarSesion -> {
                    val prefs = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
                    prefs.clear()
                    prefs.apply()

                    FirebaseAuth.getInstance().signOut()
                    showLogin()
                }
            }

            true
        }


        email = intent.extras?.getString("email")
        val imageUrl = intent.extras?.getString("image")
        provider = intent.extras?.getString("provider")

        val headerView = navView.getHeaderView(0)
        emailLbl = headerView.findViewById(R.id.emailLbl)
        imageProfile = headerView.findViewById(R.id.profileImage)
        emailLbl.text = email
        if (provider == ProviderType.GOOGLE.name){
            Glide.with(this).load(imageUrl).into(imageProfile)
        }

        setup()

        //Guardado de datos y Credenciales
        val prefs = getSharedPreferences(getString(R.string.prefs_file),Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.putString("image", imageUrl)
        prefs.apply()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setup(){
        //Navigation Setup
        val navController = findNavController()
        homeBtn = findViewById(R.id.homeBtn)
        cartBtn = findViewById(R.id.cartBtn)
        contactBtn = findViewById(R.id.contactBtn)
        barcodeBtn = findViewById(R.id.barcodeBtn)
        homeBtn.setOnClickListener {
            /*Todo: Add a check for home to don't show home again*/
            navController?.navigateUp()
            navController?.navigate(R.id.homeFragment)

        }
        cartBtn.setOnClickListener {
            navController?.navigateUp()
            navController?.navigate(R.id.carritoFragment)
        }
        contactBtn.setOnClickListener {
            navController?.navigateUp()
            navController?.navigate(R.id.contactoFragment)
        }
        barcodeBtn.setOnClickListener {
            navController?.navigateUp()
            navController?.navigate(R.id.barCodeFragment)
        }

        //Profile click handler

        imageProfile.setOnClickListener { showProfile() }
        emailLbl.setOnClickListener { showProfile() }

    }

    private fun findNavController(): NavController? {
        val navHostFragment = (this).supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as? NavHostFragment
        return navHostFragment?.navController
    }

    //Start Login activity
    private fun showLogin(){
        val loginIntent = Intent(this, LoginActivity::class.java).apply { } //Edit for new
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(loginIntent)
    }

    //Start Profile activity
    private fun showProfile(){
        val profileIntent = Intent(this, ProfileActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(profileIntent)
    }

}