package com.example.study.models

data class User(
    val id: String,
    val login: String,
    val role: String // "student" или "teacher"
)