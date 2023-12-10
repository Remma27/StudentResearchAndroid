package com.example.moviles1proyecto.ui.researchProjects

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviles1proyecto.R
import com.example.moviles1proyecto.ui.comments.Comments
import com.example.moviles1proyecto.ui.comments.MyAdapter
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Comment
import java.util.Date

class ResearchDetailsActivity : AppCompatActivity() {

    private lateinit var commentInput: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var buttonPublish: Button

    //Comentarios
    private lateinit var recyclerView: RecyclerView
    private lateinit var commentList: ArrayList<Comments>
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_research_details)
        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonPublish = findViewById<Button>(R.id.buttonPublish)
        commentInput = findViewById<EditText>(R.id.commentInput)
        ratingBar = findViewById<RatingBar>(R.id.ratingBar)

        var db = FirebaseFirestore.getInstance()
        val commentsContainer = findViewById<LinearLayout>(R.id.commentsContainer)
        val projectID = intent.getStringExtra("PROJECTID")
        val researchTitle = intent.getStringExtra("RESEARCH_TITLE")
        val areaOfInterest = intent.getStringExtra("AREA_OF_INTEREST")
        val schoolGrade = intent.getStringExtra("SCHOOL_GRADE")
        val topicDescription = intent.getStringExtra("TOPIC_DESCRIPTION")
        val conclusions = intent.getStringExtra("CONCLUSIONS")
        val finalRecomendations = intent.getStringExtra("FINAL_RECOMMENDATIONS")
        val studentID = intent.getStringExtra("STUDENTID")

        findViewById<TextView>(R.id.researchTitleDetail).text = researchTitle
        findViewById<TextView>(R.id.areaOfInterestDetail).text = areaOfInterest
        findViewById<TextView>(R.id.schoolGradeDetail).text = schoolGrade
        findViewById<TextView>(R.id.topicDescription).text = topicDescription
        findViewById<TextView>(R.id.Conclusions).text = conclusions
        findViewById<TextView>(R.id.finalRecommendations).text = finalRecomendations

        if (!studentID.isNullOrBlank()) {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    loadStudentData(db, studentID)
                } catch (e: Exception) {
                    handleStudentDataException(e)
                }
            }
        } else {
            handleStudentIDMissing()
        }

        buttonBack.setOnClickListener {
            finish()
        }

        buttonPublish.setOnClickListener {
            val firestore = FirebaseFirestore.getInstance()
            val commentText = commentInput.text.toString()
            val ratingValue = ratingBar.rating
            if (commentText.isNotEmpty()) {
                try {
                    publishComment(firestore, commentText, ratingValue, commentsContainer)
                } catch (e: Exception) {
                    handleCommentPublishException(e)
                }
            } else {
                Toast.makeText(this, "Por favor, escribe un comentario.", Toast.LENGTH_SHORT).show()
            }
        }


        //Comentarios
        recyclerView = findViewById(R.id.recyclerviewComments)
        recyclerView.layoutManager = LinearLayoutManager(this)
        commentList = arrayListOf()
        db = FirebaseFirestore.getInstance()
        db.collection("comments").whereEqualTo("projectID", projectID)
            .get()
            .addOnSuccessListener {
            if (!it.isEmpty){
                for (data in it.documents){
                    val comment:Comments? = data.toObject(Comments::class.java)
                    if (comment != null) {
                        commentList.add(comment)
                    }
                }
                recyclerView.adapter = MyAdapter(commentList)
            }
        }
            .addOnFailureListener{
                Toast.makeText(this,it.toString(),Toast.LENGTH_SHORT).show()
            }
    }

    private suspend fun loadStudentData(db: FirebaseFirestore, studentID: String) {
        val querySnapshot = db.collection("students")
            .whereEqualTo("studentID", studentID)
            .get()
            .await()

        if (!querySnapshot.isEmpty) {
            val studentDoc = querySnapshot.documents[0]
            val studentData = studentDoc.data
            val fullName = studentData?.get("fullName") as? String
            val aboutMe = studentData?.get("aboutMe") as? String
            val schoolGradeStudent = studentData?.get("schoolGrade") as? String

            findViewById<TextView>(R.id.fullName).text = fullName
            findViewById<TextView>(R.id.aboutMe).text = aboutMe
            findViewById<TextView>(R.id.schoolGradeStudent).text = schoolGradeStudent
            fullName ?: throw IllegalStateException("fullName is null or not a String")
        } else {
            throw NoSuchElementException("No student document found for studentID: $studentID")
        }
    }

    private fun publishComment(
        firestore: FirebaseFirestore,
        commentText: String,
        ratingValue: Float,
        commentsContainer: LinearLayout
    ) {
        val dt: Date = Date()
        val commentData = hashMapOf(
            "commentText" to commentText,
            "ratingValue" to ratingValue,
            "timestamp" to dt.toString()
        )

        firestore.collection("comments")
            .add(commentData)
            .addOnSuccessListener { documentReference ->
                // Register the data into the local storage
                val prefe = this.getSharedPreferences("appData", MODE_PRIVATE)
                val editor = prefe.edit()

                editor.putString("commentText", commentText)
                editor.putString("ratingValue", ratingValue.toString())
                editor.commit()
                Toast.makeText(this, "Comentario publicado.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                handleCommentPublishException(e)
            }

        val newCommentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8.dpToPx(this@ResearchDetailsActivity)
            }
        }

        val newRatingBar = RatingBar(this, null, android.R.attr.ratingBarStyleSmall).apply {
            rating = ratingValue
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8.dpToPx(context)
            }
        }

        val newCommentText = TextView(this).apply {
            text = commentText
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTextColor(resources.getColor(R.color.black))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        newCommentLayout.addView(newRatingBar)
        newCommentLayout.addView(newCommentText)

        commentsContainer.addView(newCommentLayout)

        commentInput.text.clear()
        ratingBar.rating = 0f
    }

    private fun handleStudentDataException(exception: Exception) {
        exception.printStackTrace()
        // Handle the exception according to your needs
        Toast.makeText(this, "Error cargando datos del estudiante.", Toast.LENGTH_SHORT).show()
    }

    private fun handleStudentIDMissing() {
        // Handle the case where studentID is null or empty
        Toast.makeText(
            this,
            "ID de estudiante no encontrado en el documento del proyecto.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun handleCommentPublishException(exception: Exception) {
        exception.printStackTrace()
        // Handle the exception according to your needs
        Toast.makeText(this, "Error al publicar comentario.", Toast.LENGTH_SHORT).show()
    }

    // Extension function to convert DP to PX
    private fun Int.dpToPx(context: Context): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        ).toInt()
}
