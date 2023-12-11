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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

const val valorIntentSignup = 1


class LoginActivity : AppCompatActivity() {
    var auth = FirebaseAuth.getInstance()

    private lateinit var btnAutenticar: Button
    private lateinit var txtEmail: EditText
    private lateinit var txtContra: EditText
    private lateinit var txtRegister: TextView
    var db = FirebaseFirestore.getInstance()

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnAutenticar = findViewById(R.id.btnAutenticar)
        txtEmail = findViewById(R.id.txtEmail)
        txtContra = findViewById(R.id.txtContra)
        txtRegister = findViewById(R.id.txtRegister)

        txtRegister.setOnClickListener {
            goToSignup()
        }

        btnAutenticar.setOnClickListener {
            if(txtEmail.text.isNotEmpty() && txtContra.text.isNotEmpty()){
                auth.signInWithEmailAndPassword(txtEmail.text.toString(), txtContra.text.toString()).addOnCompleteListener{
                    if (it.isSuccessful){
                        val dt: Date = Date()

                        val user = hashMapOf(
                            "ultAcceso" to dt.toString()
                        )

                        db.collection("datosUsuarios").whereEqualTo("idemp", it.result?.user?.uid.toString()).get()
                            .addOnSuccessListener { documentReference ->
                                documentReference.forEach { document ->
                                    db.collection("datosUsuarios").document(document.id).update(user as Map<String, Any>)
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this,"Error al actualizar los datos del usuario", Toast.LENGTH_SHORT).show()
                            }

                        //Register the data into the local storage
                        val prefe = this.getSharedPreferences("appData", Context.MODE_PRIVATE)

                        //Create editor object for write app data
                        val editor = prefe.edit()

                        //Set editor fields with the new values
                        editor.putString("email", txtEmail.text.toString())
                        editor.putString("contra", txtContra.text.toString())

                        //Write app data
                        editor.commit()

                        // call back to main activity
                        Intent().let {
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }else{
                        showAlert("Error","Al autenticar el usuario")
                    }
                }
            }else{
                showAlert("Error","El correo electrónico y contraseña no pueden estar vacíos")
            }
        }

        mAuth = FirebaseAuth.getInstance()

        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            // The user is already signed in, navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // finish the current activity to prevent the user from coming back to the SignInActivity using the back button
        }

        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun goToSignup() {
        val intent = Intent(this, SignupActivity::class.java)
        startActivityForResult(intent, valorIntentSignup)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // validate control variables
        if(resultCode == Activity.RESULT_OK){
            // call back to main activity
            Intent().let {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAlert(titu:String, mssg: String){
        val diagMessage = AlertDialog.Builder(this)
        diagMessage.setTitle(titu)
        diagMessage.setMessage(mssg)
        diagMessage.setPositiveButton("Aceptar", null)

        val diagVentana: AlertDialog = diagMessage.create()
        diagVentana.show()
    }

    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}