package com.example.moviles1proyecto.ui.comments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviles1proyecto.R

class MyAdapter(private val commentList: ArrayList<Comments>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentText: TextView = itemView.findViewById(R.id.commentText)
        val rating: TextView = itemView.findViewById(R.id.rating)
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.comments, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val comment = commentList[position]
        holder.commentText.text = comment.commentText
        holder.rating.text = comment.rating?.toString() ?: "N/A"

        // Check if timestamp is not null and convert it to a readable string
        val timestampString = comment.timestamp?.toDate()?.toString() ?: "N/A"
        holder.timestamp.text = timestampString
    }


}
