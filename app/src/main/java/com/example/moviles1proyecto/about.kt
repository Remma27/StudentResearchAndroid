package com.example.moviles1proyecto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class about : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val btnReturn: Button = findViewById(R.id.btnReturn)

        btnReturn.setOnClickListener {
            finish()
        }
    }
}