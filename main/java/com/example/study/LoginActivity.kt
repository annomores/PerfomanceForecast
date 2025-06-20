package com.example.study

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginEdit = findViewById<EditText>(R.id.editLogin)
        val passwordEdit = findViewById<EditText>(R.id.editPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)

        loginButton.setOnClickListener {
            val login = loginEdit.text.toString()
            val password = passwordEdit.text.toString()
            // Имитация авторизации
            when (login) {
                "student" -> {
                    startActivity(Intent(this, StudentMainActivity::class.java))
                }
                "teacher" -> {
                    startActivity(Intent(this, TeacherMainActivity::class.java))
                }
                else -> {
                    Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}