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

class MyAdapter(private val commentList: ArrayList<Comments>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentText: TextView = itemView.findViewById(R.id.commentText)
        val ratingBar: RatingBar = itemView.findViewById(R.id.rating)
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
        holder.ratingBar.rating =
            (comment.rating ?: 0.0f).toFloat()  // Asegúrate de que `rating` sea de tipo Float

        // Formatear la fecha en el formato deseado
        val timestampString = comment.timestamp?.toDate()?.let { formatDate(it) } ?: "N/A"
        holder.timestamp.text = timestampString
    }

    // Función para formatear la fecha
    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(date)
    }
}
