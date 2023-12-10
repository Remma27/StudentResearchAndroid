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

const val valorIntentLogin = 1

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var researchProjectsList: ArrayList<researchProjects>


    var auth = FirebaseAuth.getInstance()
    var email: String? = null
    var contra: String? = null

    var db = FirebaseFirestore.getInstance()

    var researchReference = db.collection("researchProjects")
    var studenReference = db.collection("students")
    var researchRef = db.document("researchProjects/FfSgeU6FCRssgRAtX594")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // intenta obtener el token del usuario del local storage, sino llama a la ventana de registro
        val prefe = getSharedPreferences("appData", Context.MODE_PRIVATE)
        email = prefe.getString("email", "")
        contra = prefe.getString("contra", "")

        if (email.toString().trim { it <= ' ' }.length == 0) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivityForResult(intent, valorIntentLogin)
        } else {
            val uid: String = auth.uid.toString()
            if (uid == "null") {
                auth.signInWithEmailAndPassword(email.toString(), contra.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Autenticación correcta", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
            obtenerDatos()
        }
    }

    private fun obtenerDatos() {
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        researchProjectsList = arrayListOf()

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