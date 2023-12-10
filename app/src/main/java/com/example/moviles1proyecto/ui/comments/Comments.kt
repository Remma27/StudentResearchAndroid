package com.example.moviles1proyecto.ui.comments

import com.google.firebase.Timestamp

data class Comments(
    val commentText: String? = null,
    val projectID: String? = null,
    val rating: Double? = null,
    val timestamp: Timestamp? = null,
    val uid: String? = null
)



