package com.example.moviles1proyecto.ui.users

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.moviles1proyecto.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class SignupActivity : AppCompatActivity() {
    // Initialize Firebase Authentication
    var auth = FirebaseAuth.getInstance()
    var db = FirebaseFirestore.getInstance()

    // UI components
    private lateinit var txtRNombre: EditText
    private lateinit var txtREmail: EditText
    private lateinit var txtRContra: EditText
    private lateinit var txtRreContra: EditText
    private lateinit var btnRegistrarU: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize UI components by finding views by their IDs
        txtRNombre = findViewById(R.id.txtRNombre)
        txtREmail = findViewById(R.id.txtREmail)
        txtRContra = findViewById(R.id.txtRContra)
        txtRreContra = findViewById(R.id.txtRreContra)
        btnRegistrarU = findViewById(R.id.btnRegistrarU)

        // Set click listener for the registration button
        btnRegistrarU.setOnClickListener {
            registrarUsuario()
        }
    }

    // Function to register a new user
    private fun registrarUsuario() {
        // Get user input from the text fields
        val nombre = txtRNombre.text.toString()
        val email = txtREmail.text.toString()
        val contra = txtRContra.text.toString()
        val reContra = txtRreContra.text.toString()

        // Check if any field is empty
        if (nombre.isEmpty() || email.isEmpty() || contra.isEmpty() || reContra.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        } else {
            // Check if passwords match
            if (contra == reContra) {
                // Create a new user in Firebase Authentication
                auth.createUserWithEmailAndPassword(email, contra)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Get current timestamp
                            val dt: Date = Date()

                            // Create user data for Firestore
                            val user = hashMapOf(
                                "idemp" to task.result?.user?.uid,
                                "usuario" to nombre,
                                "email" to email,
                                "ultAcceso" to dt.toString(),
                            )

                            // Add user data to Firestore collection
                            db.collection("userDetails")
                                .add(user)
                                .addOnSuccessListener { documentReference ->

                                    // Register the data into the local storage
                                    val preferences = this.getSharedPreferences("appData", Context.MODE_PRIVATE)

                                    // Create an editor object to write app data
                                    val editor = preferences.edit()

                                    // Set editor fields with the new values
                                    editor.putString("email", email.toString())
                                    editor.putString("contra", contra.toString())

                                    // Write app data
                                    editor.commit()

                                    Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()

                                    // Callback to the calling activity
                                    Intent().let {
                                        setResult(Activity.RESULT_OK)
                                        finish()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error registering user", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Error registering user", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
