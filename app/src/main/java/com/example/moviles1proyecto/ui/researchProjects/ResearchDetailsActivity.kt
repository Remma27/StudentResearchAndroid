package com.example.moviles1proyecto.ui.researchProjects

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.example.moviles1proyecto.R

import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date


class ResearchDetailsActivity : AppCompatActivity() {

    private lateinit var commentInput:EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var buttonPublish: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_research_details)
        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonPublish = findViewById<Button>(R.id.buttonPublish)
        commentInput = findViewById<EditText>(R.id.commentInput)
        ratingBar = findViewById<RatingBar>(R.id.ratingBar)



        val commentsContainer = findViewById<LinearLayout>(R.id.commentsContainer)
        // Recoger los datos pasados en el Intent
        val researchTitle = intent.getStringExtra("RESEARCH_TITLE")
        val areaOfInterest = intent.getStringExtra("AREA_OF_INTEREST")
        val schoolGrade = intent.getStringExtra("SCHOOL_GRADE")
        val topicDescription = intent.getStringExtra("TOPIC_DESCRIPTION")
        val conclusions = intent.getStringExtra("CONCLUSIONS")
        val finalRecomendations = intent.getStringExtra("FINAL_RECOMMENDATIONS")

        // Configurar las vistas con los datos recibidos
        findViewById<TextView>(R.id.researchTitleDetail).text = researchTitle
        findViewById<TextView>(R.id.areaOfInterestDetail).text = areaOfInterest
        findViewById<TextView>(R.id.schoolGradeDetail).text = schoolGrade
        findViewById<TextView>(R.id.topicDescription).text = topicDescription
        findViewById<TextView>(R.id.Conclusions).text = conclusions
        findViewById<TextView>(R.id.finalRecommendations).text = finalRecomendations



        buttonBack.setOnClickListener {
            // Cierra esta Activity y regresa a la anterior en el stack
            finish()
        }


        buttonPublish.setOnClickListener {
            val firestore = FirebaseFirestore.getInstance()
            val commentText = commentInput.text.toString()
            val ratingValue = ratingBar.rating
            if (commentText.isNotEmpty()) {
                // Preparar el objeto de comentario para subirlo
                val dt: Date = Date()
                val commentData = hashMapOf(
                    "commentText" to commentText,
                    "ratingValue" to ratingValue,
                    "timestamp" to dt.toString()
                )
                // Subir el objeto a la colección de comentarios
                firestore.collection("comments")
                    .add(commentData)
                    .addOnSuccessListener { documentReference ->
                        //Register the data into the local storage
                        val prefe = this.getSharedPreferences("appData", Context.MODE_PRIVATE)
                        //Create editor object for write app data
                        val editor = prefe.edit()


                        //Set editor fields with the new values
                        editor.putString("commentText", commentText.toString())
                        editor.putString("ratingValue", ratingValue.toString())
                        editor.commit()
                        Toast.makeText(this, "Comentario publicado.", Toast.LENGTH_SHORT).show()

                    }
                    .addOnFailureListener { e ->
                        // Acciones a realizar si hay un error al subir el comentario
                        Log.w(TAG, "Error añadiendo el documento", e)
                        Toast.makeText(this, "Error al publicar comentario.", Toast.LENGTH_SHORT).show()
                    }


                // Crear un nuevo LinearLayout para contener el comentario y la valoración
                val newCommentLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 8.dpToPx(this@ResearchDetailsActivity)
                    }
                }
                // Crear y configurar RatingBar para la valoración
                val newRatingBar = RatingBar(this, null, android.R.attr.ratingBarStyleSmall).apply {
                    rating = ratingValue
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 8.dpToPx(context)
                    }
                }
                // Crear y configurar TextView para el comentario
                val newCommentText = TextView(this).apply {
                    text = commentText
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    setTextColor(resources.getColor(R.color.black))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                // Agregar TextView y RatingBar al LinearLayout
                newCommentLayout.addView(newRatingBar)
                newCommentLayout.addView(newCommentText)


                // Agregar el LinearLayout al contenedor de comentarios
                commentsContainer.addView(newCommentLayout)

                // Limpiar el campo de texto y el RatingBar para el próximo comentario
                commentInput.text.clear()
                ratingBar.rating = 0f


            } else {
                Toast.makeText(this, "Por favor, escribe un comentario.", Toast.LENGTH_SHORT).show()
            }
        }

    }

// Función de extensión para convertir DP a PX
fun Int.dpToPx(context: Context): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics).toInt()}


