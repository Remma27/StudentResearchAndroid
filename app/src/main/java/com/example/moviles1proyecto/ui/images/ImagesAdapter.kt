package com.example.moviles1proyecto.ui.images

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class ImagesAdapter(private val context: Context) {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    private val uploadedImageUrls = mutableSetOf<String>() // Nuevo conjunto para URLs
    private var imageCount = 0

    fun addImage(imageUri: Uri) {
        imageCount++
        uploadedImageUrls.remove(imageUri.toString()) // Permitir subir imágenes que fueron seleccionadas previamente
    }

    fun uploadImages() {
        for (imageCount in 1..imageCount) {
            // Genera un ID único para la imagen
            val imageID = UUID.randomUUID().toString()

            // Sube la imagen a Firebase Storage
            val imageRef: StorageReference = storageRef.child("images/$imageID.jpg")
            // Puedes agregar lógica adicional aquí si es necesario

            // Simula la subida y obtención de la URL (reemplazar con tu lógica real)
            val uri = "https://example.com/$imageID.jpg"

            // Guarda la URL en el conjunto de imágenes subidas
            uploadedImageUrls.add(uri)

            // Guarda los detalles en Firestore
            val imageDetails = hashMapOf(
                "imageID" to imageID,
                "image" to uri
            )

            // Guarda los detalles en Firestore
            db.collection("images").add(imageDetails)
                .addOnSuccessListener { documentReference ->
                    // La imagen y los detalles se han guardado exitosamente
                    showUploadCompleteToast()
                    // Reinicia la cuenta de imágenes después de la subida
                    this.imageCount = 0
                }
                .addOnFailureListener { e ->
                    // Maneja el fallo en caso de error
                }
        }
    }

    // Función para mostrar un mensaje de notificación cuando las imágenes se han subido
    private fun showUploadCompleteToast() {
        Toast.makeText(
            context,
            "Imágenes subidas",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Función para obtener la cantidad de imágenes seleccionadas
    fun getImageCount(): Int {
        return imageCount
    }
}
