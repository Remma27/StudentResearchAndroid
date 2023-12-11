package com.example.moviles1proyecto.ui.comments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moviles1proyecto.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Adapter class for managing and displaying comments in a RecyclerView
class MyAdapter(private val commentList: ArrayList<Comments>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    // ViewHolder class to hold references to the views within the item layout
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentText: TextView = itemView.findViewById(R.id.commentText)
        val ratingBar: RatingBar = itemView.findViewById(R.id.rating)
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
    }

    // Called when RecyclerView needs a new ViewHolder of the given type to represent an item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate the item layout and create a new ViewHolder
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.comments, parent, false)
        return MyViewHolder(itemView)
    }

    // Returns the total number of items in the data set held by the adapter
    override fun getItemCount(): Int {
        return commentList.size
    }

    // Called by RecyclerView to display the data at the specified position
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Get the comment at the current position
        val comment = commentList[position]

        // Set the comment text, rating, and timestamp to the corresponding views in the ViewHolder
        holder.commentText.text = comment.commentText
        holder.ratingBar.rating =
            (comment.rating ?: 0.0f).toFloat()

        // Convert timestamp to a formatted date string or display "N/A" if timestamp is null
        val timestampString = comment.timestamp?.toDate()?.let { formatDate(it) } ?: "N/A"
        holder.timestamp.text = timestampString
    }

    // Private function to format a Date object into a string with the format "dd/MM/yyyy"
    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(date)
    }
}
