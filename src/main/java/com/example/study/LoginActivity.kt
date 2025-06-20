package com.example.study

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val STUDENT_LOGIN = "student"
        private const val STUDENT_PASSWORD = "student123"
        private const val TEACHER_LOGIN = "teacher"
        private const val TEACHER_PASSWORD = "teacher123"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginEdit = findViewById<EditText>(R.id.editLogin)
        val passwordEdit = findViewById<EditText>(R.id.editPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)

        loginButton.setOnClickListener {
            val login = loginEdit.text.toString()
            val password = passwordEdit.text.toString()
            when {
                login == STUDENT_LOGIN && password == STUDENT_PASSWORD -> {
                    startActivity(Intent(this, StudentMainActivity::class.java))
                }
                login == TEACHER_LOGIN && password == TEACHER_PASSWORD -> {
                    startActivity(Intent(this, TeacherMainActivity::class.java))
                }
                else -> {
                    Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}