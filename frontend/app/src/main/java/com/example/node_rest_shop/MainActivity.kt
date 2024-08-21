package com.example.node_rest_shop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener { login() }
    }

    private fun login() {
        val url = "http://192.168.100.81:3000/user/login" // Reemplaza X con tu IP local

        val jsonBody = JSONObject().apply {
            put("email", etEmail.text.toString())
            put("password", etPassword.text.toString())
        }

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                Log.d("MainActivity", "Login exitoso")
                Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Iniciando ProductListActivity")
                startActivity(Intent(this, ProductListActivity::class.java))
                finish()
            },
            { error ->
                Log.e("MainActivity", "Error en login: ${error.message}")
                Toast.makeText(this, "Error en login: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        // Añadir la solicitud a la cola
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }
}