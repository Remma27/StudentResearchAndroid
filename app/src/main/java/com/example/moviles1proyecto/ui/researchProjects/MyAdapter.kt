package com.example.moviles1proyecto.ui.researchProjects

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviles1proyecto.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Adapter class for managing and displaying research projects in a RecyclerView
class MyAdapter(private val researchProjectsList: ArrayList<researchProjects>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // ViewHolder class to hold references to the views within the item layout
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val researchTitle: TextView = itemView.findViewById(R.id.researchTitle)
        val areaOfInterest: TextView = itemView.findViewById(R.id.areaOfInterest)
        val schoolGrade: TextView = itemView.findViewById(R.id.schoolGrade)
        val studentName: TextView = itemView.findViewById(R.id.studentFullName)

        // Function to bind data to the ViewHolder
        fun bind(researchProject: researchProjects) {
            // Set click listener on the card view to open ResearchDetailsActivity
            itemView.findViewById<CardView>(R.id.cardView).setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, ResearchDetailsActivity::class.java)

                // Pass data related to the selected research project to the intent
                intent.putExtra("PROJECTID", researchProject.projectID)
                intent.putExtra("STUDENTID", researchProject.studentID)
                intent.putExtra("RESEARCH_TITLE", researchProject.researchTitle)
                intent.putExtra("AREA_OF_INTEREST", researchProject.areaOfInterest)
                intent.putExtra("SCHOOL_GRADE", researchProject.schoolGrade)
                intent.putExtra("TOPIC_DESCRIPTION", researchProject.topicDescription)
                intent.putExtra("CONCLUSIONS", researchProject.conclusions)
                intent.putExtra("FINAL_RECOMMENDATIONS", researchProject.finalRecommendations)
                intent.putExtra("PDFURL",researchProject.pdfUrl)
                //gets the list of investigations images and and sends it to the researchDetailsActivity
                intent.putStringArrayListExtra("IMAGES", ArrayList(researchProject.images))
                context.startActivity(intent)
            }

            val currentViewHolder = this

            // Use coroutines to asynchronously load student name
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    loadStudentName(researchProject.studentID, currentViewHolder)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // Function to load student name from Firestore
        private suspend fun loadStudentName(studentID: String?, holder: MyViewHolder) {
            val db = FirebaseFirestore.getInstance()

            // Query Firestore to get student data based on studentID
            val querySnapshot = db.collection("students")
                .whereEqualTo("studentID", studentID)
                .get()
                .await()

            // Check if the query result is not empty
            if (!querySnapshot.isEmpty) {
                val studentDoc = querySnapshot.documents[0]
                val studentData = studentDoc.data
                val fullName = studentData?.get("fullName") as? String
                holder.studentName.text = fullName
                fullName ?: throw IllegalStateException("fullName is null or not a String")
            } else {
                throw NoSuchElementException("No student document found for studentID: $studentID")
            }
        }
    }

    // Called when RecyclerView needs a new ViewHolder of the given type to represent an item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate the item layout and create a new ViewHolder
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    // Returns the total number of items in the data set held by the adapter
    override fun getItemCount(): Int {
        return researchProjectsList.size
    }

    // Called by RecyclerView to display the data at the specified position
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Set values for the views in the ViewHolder based on the research project at the current position
        holder.researchTitle.text = researchProjectsList[position].researchTitle
        holder.areaOfInterest.text = researchProjectsList[position].areaOfInterest
        holder.schoolGrade.text = researchProjectsList[position].schoolGrade
        holder.studentName.text = ""

        // Get the research project at the current position and bind data to the ViewHolder
        val researchProject = researchProjectsList[position]
        holder.bind(researchProject)
    }
}
