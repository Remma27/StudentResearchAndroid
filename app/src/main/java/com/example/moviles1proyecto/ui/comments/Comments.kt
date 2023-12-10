package com.example.moviles1proyecto.ui.comments

import com.google.firebase.Timestamp

data class Comments(
    val commentText: String? = null,
    val projectID: String? = null,
    val rating: Double? = null,
    val timestamp: Timestamp? = null,  // Change the type to Timestamp
    val uid: String? = null
)



