package com.example.study

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.study.models.StudentStats

class TeacherMainActivity : AppCompatActivity() {
    private val subjects = listOf("ООП", "Мат. анализ", "Дифф. уравнения")
    private val groups = listOf("22", "23")
    private val students = listOf(
        Pair("Иванов И.И.", StudentStats("ООП", 4.5, 2, "Сдаст")),
        Pair("Петров П.П.", StudentStats("ООП", 3.2, 6, "Не сдаст")),
        Pair("Сидоров С.С.", StudentStats("ООП", 4.0, 1, "Сдаст"))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_main)

        val spinnerSubjects = findViewById<Spinner>(R.id.spinnerSubjects)
        val spinnerGroups = findViewById<Spinner>(R.id.spinnerGroups)
        val layoutStudents = findViewById<LinearLayout>(R.id.layoutStudents)

        spinnerSubjects.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, subjects)
        spinnerGroups.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, groups)

        fun updateStudents() {
            layoutStudents.removeAllViews()
            for ((name, stat) in students) {
                val tv = TextView(this)
                tv.text = "$name — Прогноз: ${stat.forecast} (${stat.currentGrade} балл, ${stat.missedLectures} пропущено)"
                layoutStudents.addView(tv)
            }
        }

        spinnerSubjects.setSelection(0)
        spinnerGroups.setSelection(0)
        updateStudents()
    }
}