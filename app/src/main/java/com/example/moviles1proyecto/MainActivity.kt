package com.example.moviles1proyecto.ui.users

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviles1proyecto.R
import com.example.moviles1proyecto.ui.researchProjects.MyAdapter
import com.example.moviles1proyecto.ui.researchProjects.researchProjects
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Constant to identify the login intent result
const val loginIntentResult = 1

class MainActivity : AppCompatActivity() {

    // UI components
    private lateinit var recyclerView: RecyclerView
    private lateinit var researchProjectsList: ArrayList<researchProjects>

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
}
