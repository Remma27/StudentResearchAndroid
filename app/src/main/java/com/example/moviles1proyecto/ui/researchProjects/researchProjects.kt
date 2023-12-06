package com.example.moviles1proyecto.ui.researchProjects


data class researchProjects(
    val projectID: String? = null,
    val researchTitle: String? = null,
    val areaOfInterest: String? = null,
    val topicDescription: String? = null,
    val pdfUrl: String? = null,
    val images: List<String>? = null,
    val conclusions: String? = null,
    val finalRecommendations: String? = null
)
