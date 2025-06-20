package com.example.study

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class TeacherMainActivity : AppCompatActivity() {

    private val subjects = listOf("ООП", "Мат. анализ", "Дифф. уравнения")
    private val groups = listOf("22", "23")

    // Пример данных студентов
    private val students = listOf(
        StudentRow("Иванов И.И.", false),
        StudentRow("Петров П.П.", true),
        StudentRow("Сидоров С.С.", false),
        StudentRow("Кузнецов К.К.", true),
        StudentRow("Смирнов С.С.", false)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_main)

        val btnOOP = findViewById<Button>(R.id.btnOOP)
        val btnMath = findViewById<Button>(R.id.btnMath)
        val btnDiff = findViewById<Button>(R.id.btnDiff)
        val spinnerGroups = findViewById<Spinner>(R.id.spinnerGroups)
        val tableStudents = findViewById<TableLayout>(R.id.tableStudents)

        // Кнопки-предметы
        fun selectSubject(index: Int) {
            btnOOP.background =
                getDrawable(if (index == 0) R.drawable.subject_btn_selected else R.drawable.subject_btn_unselected)
            btnMath.background =
                getDrawable(if (index == 1) R.drawable.subject_btn_selected else R.drawable.subject_btn_unselected)
            btnDiff.background =
                getDrawable(if (index == 2) R.drawable.subject_btn_selected else R.drawable.subject_btn_unselected)
            btnOOP.setTypeface(null, if (index == 0) Typeface.BOLD else Typeface.NORMAL)
            btnMath.setTypeface(null, if (index == 1) Typeface.BOLD else Typeface.NORMAL)
            btnDiff.setTypeface(null, if (index == 2) Typeface.BOLD else Typeface.NORMAL)
            // Здесь можно обновлять данные студентов по предмету
        }
        btnOOP.setOnClickListener { selectSubject(0) }
        btnMath.setOnClickListener { selectSubject(1) }
        btnDiff.setOnClickListener { selectSubject(2) }
        selectSubject(0)

        // Группы
        spinnerGroups.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, groups)

        // Таблица студентов
        fun fillTable() {
            // Удаляем старые строки, кроме заголовка
            while (tableStudents.childCount > 1) {
                tableStudents.removeViewAt(1)
            }
            for (student in students) {
                val row = TableRow(this)
                row.layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                // ФИО
                val tvName = TextView(this)
                tvName.text = student.name
                tvName.setPadding(8, 8, 8, 8)
                tvName.setTextColor(Color.BLACK)
                tvName.textSize = 16f
                tvName.typeface = Typeface.SERIF

                // Прогноз
                val tvForecast = TextView(this)
                tvForecast.text = if (student.pass) "Сдаст зачёт" else "Не сдаст зачёт"
                tvForecast.setPadding(8, 8, 8, 8)
                tvForecast.textSize = 16f
                tvForecast.typeface = Typeface.SERIF
                tvForecast.setTextColor(
                    if (student.pass) Color.parseColor("#178B4B") else Color.parseColor(
                        "#B22222"
                    )
                )
                // Кнопки (заглушки)
                val btnEdit = ImageView(this)
                btnEdit.setImageResource(android.R.drawable.ic_menu_edit)
                btnEdit.setPadding(8, 8, 8, 8)
                val btnOpen = ImageView(this)
                btnOpen.setImageResource(android.R.drawable.ic_menu_view)
                btnOpen.setPadding(8, 8, 8, 8)
                // Добавляем в строку
                row.addView(tvName)
                row.addView(tvForecast)
                row.addView(btnEdit)
                row.addView(btnOpen)
                tableStudents.addView(row)
            }
        }
        fillTable()

        // Нижнее меню
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationTeacher)

        bottomNav.itemIconTintList = ContextCompat.getColorStateList(this, R.color.ic_dialog_dialer)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_teacher_dashboard -> {

                    true
                }

                else -> false
            }
        }
    }
}

        data class StudentRow(val name: String, val pass: Boolean)
