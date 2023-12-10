package com.example.moviles1proyecto.ui.users

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.moviles1proyecto.R
import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

// Constant to identify the signup intent result
const val signupIntentResult = 1

// LoginActivity class for user authentication
class LoginActivity : AppCompatActivity() {
    // Initialize Firebase Authentication
    var auth = FirebaseAuth.getInstance()

    private lateinit var btnAuthenticate: Button
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var txtRegister: TextView
    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize UI components
        btnAuthenticate = findViewById(R.id.btnAutenticar)
        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtContra)
        txtRegister = findViewById(R.id.txtRegister)

        // Set click listener for navigating to the signup activity
        txtRegister.setOnClickListener {
            goToSignup()
        }

        // Set click listener for authentication
        btnAuthenticate.setOnClickListener {
            // Check if email and password fields are not empty
            if (txtEmail.text.isNotEmpty() && txtPassword.text.isNotEmpty()) {
                auth.signInWithEmailAndPassword(txtEmail.text.toString(), txtPassword.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            // Update the last access timestamp for the user
                            val dt: Date = Date()
                            val user = hashMapOf("lastAccess" to dt.toString())

                            db.collection("userDetails").whereEqualTo("userId", it.result?.user?.uid.toString()).get()
                                .addOnSuccessListener { documentReference ->
                                    documentReference.forEach { document ->
                                        db.collection("userDetails").document(document.id).update(user as Map<String, Any>)
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error updating user details", Toast.LENGTH_SHORT).show()
                                }

                            // Register the data into local storage
                            val preferences = this.getSharedPreferences("appData", Context.MODE_PRIVATE)

                            // Create an editor object to write app data
                            val editor = preferences.edit()

                            // Set editor fields with the new values
                            editor.putString("email", txtEmail.text.toString())
                            editor.putString("password", txtPassword.text.toString())

                            // Write app data
                            editor.apply()

                            // Callback to the main activity
                            Intent().let {
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        } else {
                            showAlert("Error", "Authentication failed")
                        }
                    }
            } else {
                showAlert("Error", "Email and password cannot be empty")
            }
        }
    }

    // Function to navigate to the signup activity
    private fun goToSignup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivityForResult(intent, signupIntentResult)
    }

    // Handle the result of the signup activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Validate control variables
        if (resultCode == Activity.RESULT_OK) {
            // Callback to the main activity
            Intent().let {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    // Function to show an alert dialog
    private fun showAlert(title: String, message: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("OK", null)

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
