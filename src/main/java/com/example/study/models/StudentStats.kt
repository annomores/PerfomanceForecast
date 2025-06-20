package com.example.study.models

data class StudentStats(
    val subject: String,
    val currentGrade: Double,
    val missedLectures: Int,
    val forecast: String // "Сдаст" или "Не сдаст"
)

