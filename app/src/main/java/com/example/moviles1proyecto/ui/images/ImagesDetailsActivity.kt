package com.example.moviles1proyecto.ui.images

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.example.moviles1proyecto.R
import org.checkerframework.common.returnsreceiver.qual.This

class ImagesDetailsActivity : AppCompatActivity() {
    private val imagesAdapter = ImagesAdapter(this)
    private val PICK_IMAGE_REQUEST = 1
    private val REQUEST_IMAGE_CAPTURE = 2
    private val MY_CAMERA_PERMISSION_CODE = 100
    private val CAMERA_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_details)

        // Botón para seleccionar imagen
        val selectImageButton = findViewById<Button>(R.id.selectImageButton)
        selectImageButton.setOnClickListener {
            // Abre la galería para seleccionar una imagen
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        val takePictureButton = findViewById<Button>(R.id.takePictureButton)
        takePictureButton.setOnClickListener {
            requestCameraPermission()
        }

        // Botón para subir imágenes al Firestore
        val uploadButton = findViewById<Button>(R.id.uploadButton)
        uploadButton.setOnClickListener {
            if (imagesAdapter.getImageCount() > 0) {
                imagesAdapter.uploadImages()
                showUploadCompleteToast()
            } else {
                showToast("Por favor, seleccione imágenes")
            }
        }
    }

    // Función para abrir la cámara
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    // Maneja el resultado de la selección de imágenes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == PICK_IMAGE_REQUEST || requestCode == CAMERA_REQUEST_CODE)
            && resultCode == Activity.RESULT_OK && data != null
        ) {
            // Obtiene la URI de la imagen seleccionada desde la galería o la cámara
            val imageUri: Uri = data.data ?: Uri.EMPTY

            // Llama a addImage cuando el usuario haya seleccionado una imagen
            imagesAdapter.addImage(imageUri)

            // Muestra un mensaje indicando la cantidad de imágenes seleccionadas
            val message = "Has seleccionado ${imagesAdapter.getImageCount()} imagenes"
            showToast(message)
        }
    }

    // Función para solicitar permisos de cámara en tiempo de ejecución
    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                MY_CAMERA_PERMISSION_CODE
            )
        } else {
            openCamera()
        }
    }

    // Función para mostrar un mensaje de notificación
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Función para mostrar un mensaje de notificación cuando las imágenes se han subido
    private fun showUploadCompleteToast() {
        Toast.makeText(this, "Imágenes subidas", Toast.LENGTH_SHORT).show()
    }
}
