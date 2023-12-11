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

class MyAdapter(private val researchProjectsList: ArrayList<researchProjects>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val researchTitle: TextView = itemView.findViewById(R.id.researchTitle)
        val areaOfInterest: TextView = itemView.findViewById(R.id.areaOfInterest)
        val schoolGrade: TextView = itemView.findViewById(R.id.schoolGrade)
        val studentName: TextView = itemView.findViewById(R.id.studentFullName)

        fun bind(researchProject: researchProjects) {
            itemView.findViewById<CardView>(R.id.cardView).setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, ResearchDetailsActivity::class.java)
                intent.putExtra("PROJECTID",researchProject.projectID)
                intent.putExtra("STUDENTID", researchProject.studentID)
                intent.putExtra("RESEARCH_TITLE", researchProject.researchTitle)
                intent.putExtra("AREA_OF_INTEREST", researchProject.areaOfInterest)
                intent.putExtra("SCHOOL_GRADE", researchProject.schoolGrade)
                intent.putExtra("TOPIC_DESCRIPTION", researchProject.topicDescription)
                intent.putExtra("CONCLUSIONS", researchProject.conclusions)
                intent.putExtra("FINAL_RECOMMENDATIONS", researchProject.finalRecommendations)
                //gets the list of investigations images and and sends it to the researchDetailsActivity
                intent.putStringArrayListExtra("IMAGES", ArrayList(researchProject.images))
                context.startActivity(intent)
            }

            // Almacenar la referencia al MyViewHolder actual
            val currentViewHolder = this

            // Cargar el nombre del estudiante de manera asíncrona
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    loadStudentName(researchProject.studentID, currentViewHolder)
                } catch (e: Exception) {
                    // Manejar la excepción según tus necesidades
                    e.printStackTrace()
                }
            }
        }

        private suspend fun loadStudentName(studentID: String?, holder: MyViewHolder) {

            val db = FirebaseFirestore.getInstance()
            val querySnapshot = db.collection("students")
                .whereEqualTo("studentID", studentID)
                .get()
                .await()

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return researchProjectsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.researchTitle.text = researchProjectsList[position].researchTitle
        holder.areaOfInterest.text = researchProjectsList[position].areaOfInterest
        holder.schoolGrade.text = researchProjectsList[position].schoolGrade
        holder.studentName.text = ""
        val researchProject = researchProjectsList[position]
        holder.bind(researchProject)
    }
}
