package com.example.moviles1proyecto.ui.researchProjects

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Activity for displaying details of a research project and handling comments
class ResearchDetailsActivity : AppCompatActivity() {

    private lateinit var commentInput: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var buttonPublish: Button

    // Comments
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

        // Initialize Firebase
        var db = FirebaseFirestore.getInstance()

        // Get data passed from the previous activity
        val projectID = intent.getStringExtra("PROJECTID")
        val researchTitle = intent.getStringExtra("RESEARCH_TITLE")
        val areaOfInterest = intent.getStringExtra("AREA_OF_INTEREST")
        val schoolGrade = intent.getStringExtra("SCHOOL_GRADE")
        val topicDescription = intent.getStringExtra("TOPIC_DESCRIPTION")
        val conclusions = intent.getStringExtra("CONCLUSIONS")
        val finalRecomendations = intent.getStringExtra("FINAL_RECOMMENDATIONS")
        val studentID = intent.getStringExtra("STUDENTID")

        // Set details in the UI
        findViewById<TextView>(R.id.researchTitleDetail).text = researchTitle
        findViewById<TextView>(R.id.areaOfInterestDetail).text = areaOfInterest
        findViewById<TextView>(R.id.schoolGradeDetail).text = schoolGrade
        findViewById<TextView>(R.id.topicDescription).text = topicDescription
        findViewById<TextView>(R.id.Conclusions).text = conclusions
        findViewById<TextView>(R.id.finalRecommendations).text = finalRecomendations

        // Load student data asynchronously
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

        // Back button
        buttonBack.setOnClickListener {
            finish()
        }

        // Publish a new comment
        buttonPublish.setOnClickListener {
            val firestore = FirebaseFirestore.getInstance()
            val commentText = commentInput.text.toString()
            val ratingValue = ratingBar.rating

            if (commentText.isNotEmpty()) {
                try {
                    val currentUser = FirebaseAuth.getInstance().currentUser

                    if (currentUser != null) {
                        val commentData = hashMapOf(
                            "commentText" to commentText,
                            "rating" to ratingValue.toDouble(),
                            "timestamp" to FieldValue.serverTimestamp(),
                            "projectID" to projectID,
                            "uid" to currentUser.uid
                        )

                        // Check if the user has commented on this project before
                        db.collection("comments")
                            .whereEqualTo("uid", currentUser.uid)
                            .whereEqualTo("projectID", projectID)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                if (querySnapshot.isEmpty) {
                                    // Save the comment only if the user has not commented on this project before
                                    db.collection("comments")
                                        .add(commentData)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this,
                                                "Comment published successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            commentInput.text.clear()
                                            ratingBar.rating = 0f
                                            recreate()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                this,
                                                "Error publishing comment: $e",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "You have already commented on this project.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    this,
                                    "Error checking previous comments: $e",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Please log in to leave a comment.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error publishing comment.", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, "Please write a comment.", Toast.LENGTH_SHORT).show()
            }
        }

        // View Comments
        recyclerView = findViewById(R.id.recyclerviewComments)
        recyclerView.layoutManager = LinearLayoutManager(this)
        commentList = arrayListOf()
        db = FirebaseFirestore.getInstance()
        db.collection("comments").whereEqualTo("projectID", projectID)
            .get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        val comment: Comments? = data.toObject(Comments::class.java)
                        if (comment != null) {
                            commentList.add(comment)
                        }
                    }
                    recyclerView.adapter = MyAdapter(commentList)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    // Function to load student data from Firestore
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

    // Function to handle exceptions when loading student data
    private fun handleStudentDataException(exception: Exception) {
        exception.printStackTrace()
        // Handle the exception according to your needs
        Toast.makeText(this, "Error loading student data.", Toast.LENGTH_SHORT).show()
    }

    // Function to handle the case when studentID is missing
    private fun handleStudentIDMissing() {
        // Handle the case where studentID is null or empty
        Toast.makeText(
            this,
            "Student ID not found in the project document.",
            Toast.LENGTH_SHORT
        ).show()
    }
}
