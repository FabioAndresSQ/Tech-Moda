package com.techmoda.view.ui.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.techmoda.ProviderType
import com.techmoda.R
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {
    private lateinit var profileImage : CircleImageView
    private lateinit var profileNameTxt : EditText
    private lateinit var profileEmailTxt : EditText
    private lateinit var saveChangesBtn : Button
    private var email: String? = null
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profileImage = findViewById(R.id.profileImage)
        profileNameTxt = findViewById(R.id.profileNameTxt)
        profileEmailTxt = findViewById(R.id.profileEmailTxt)
        saveChangesBtn = findViewById(R.id.saveChangesBtn)

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        email = prefs.getString("email", null)
        val savedImage = prefs.getString("image", null)
        val provider = prefs.getString("provider", null)
        profileEmailTxt.setText(email)
        if (provider == ProviderType.GOOGLE.name){
            Glide.with(this).load(savedImage).into(profileImage)
        }

        db.collection("users").document(email.toString()).get().addOnSuccessListener {
            profileNameTxt.setText(it.get("nombre") as String?)
        }

        saveChangesBtn.setOnClickListener {

            if (provider == ProviderType.GOOGLE.name){
                db.collection("users").document(email.toString()).set(
                    hashMapOf("provider" to ProviderType.GOOGLE.name,
                        "nombre" to profileNameTxt.text.toString())
                )
            } else {
                db.collection("users").document(email.toString()).set(
                    hashMapOf("provider" to ProviderType.BASIC.name,
                        "nombre" to profileNameTxt.text.toString())
                )
            }
            Toast.makeText(this,"Guardado",Toast.LENGTH_SHORT).show()

        }

    }
}