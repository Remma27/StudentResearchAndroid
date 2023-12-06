package com.example.moviles1proyecto.ui.researchProjects
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviles1proyecto.R

class MyAdapter(private val researchProjectsList: ArrayList<researchProjects>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val researchTitle: TextView = itemView.findViewById(R.id.researchTitle)
        val areaOfInterest: TextView = itemView.findViewById(R.id.areaOfInterest)
        val schoolGrade: TextView = itemView.findViewById(R.id.schoolGrade)
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
    }
}
