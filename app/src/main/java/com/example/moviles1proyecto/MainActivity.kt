package com.example.moviles1proyecto.ui.users

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviles1proyecto.R
import com.example.moviles1proyecto.about
import com.example.moviles1proyecto.ui.images.ImagesDetailsActivity
import com.example.moviles1proyecto.ui.researchProjects.MyAdapter
import com.example.moviles1proyecto.ui.researchProjects.researchProjects
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

// Constant to identify the login intent result
const val loginIntentResult = 1

class MainActivity : AppCompatActivity() {

    // UI components
    private lateinit var recyclerView: RecyclerView
    private lateinit var researchProjectsList: ArrayList<researchProjects>
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth


    // Firebase Authentication and Firestore instances
    var auth = FirebaseAuth.getInstance()
    var email: String? = null
    var contra: String? = null
    var db = FirebaseFirestore.getInstance()

    // Firestore references
    var researchReference = db.collection("researchProjects")
    var studentReference = db.collection("students")
    var researchRef = db.document("researchProjects/FfSgeU6FCRssgRAtX594")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Try to get the user token from local storage
        // If not found, call the login window
        val preferences = getSharedPreferences("appData", Context.MODE_PRIVATE)
        email = preferences.getString("email", "")
        contra = preferences.getString("contra", "")

        if (email.toString().trim { it <= ' ' }.isEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, loginIntentResult)
        } else {
            val uid: String = auth.uid.toString()
            if (uid == "null") {
                // If the user is not authenticated, sign in using stored credentials
                auth.signInWithEmailAndPassword(email.toString(), contra.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Authentication successful", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            obtenerDatos()
        }

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val auth = Firebase.auth
        val user = auth.currentUser

        if (user != null) {
            val userName = user.displayName
            //textView.text = "Welcome, " + userName
        } else {
            // Handle the case where the user is not signed in
        }

        val btnAboutUs: Button = findViewById(R.id.btn1)

        btnAboutUs.setOnClickListener {
            val intent = Intent(this, about::class.java)
            startActivity(intent)
        }

        val btnGallery: Button = findViewById(R.id.btn2)

        btnGallery.setOnClickListener {
            val intent = Intent(this, ImagesDetailsActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to retrieve data from Firestore and populate the RecyclerView
    private fun obtenerDatos() {
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        researchProjectsList = arrayListOf()

        // Retrieve research projects data from Firestore
        db.collection("researchProjects")
            .get()
            .addOnSuccessListener { documents ->
                val projects = documents.toObjects(researchProjects::class.java)
                researchProjectsList.addAll(projects)
                recyclerView.adapter = MyAdapter(researchProjectsList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    fun irAActividadDeImagenes(view: View) {
        val intent = Intent(this, ImagesDetailsActivity::class.java)
        startActivity(intent)
    }

    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            // Optional: Update UI or show a message to the user
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}

