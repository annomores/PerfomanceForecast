package com.example.study

import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class StudentMainActivity : AppCompatActivity() {

    // Пример данных для трёх предметов
    private val subjects = listOf("ООП", "Мат. анализ", "Дифф. уравнения")
    private val stats = listOf(
        SubjectStats("Прогноз по предмету - 3 семестр", "Вы успешно сдадите сессию", 4.5, 2),
        SubjectStats("Прогноз по предмету - 3 семестр", "Вы успешно сдадите сессию", 3.9, 5),
        SubjectStats("Прогноз по предмету - 3 семестр", "Вы успешно сдадите сессию", 4.2, 1)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_main)

        val btnOOP = findViewById<Button>(R.id.btnOOP)
        val btnMath = findViewById<Button>(R.id.btnMath)
        val btnDiff = findViewById<Button>(R.id.btnDiff)

        val textForecastTitle = findViewById<TextView>(R.id.textForecastTitle)
        val textForecast = findViewById<TextView>(R.id.textForecast)
        val textGrade = findViewById<TextView>(R.id.textGrade)
        val textMissed = findViewById<TextView>(R.id.textMissed)

        // Функция выбора предмета
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

            // Данные
            val stat = stats[index]
            textForecastTitle.text = stat.forecastTitle
            textForecast.text = stat.forecastText
            textGrade.text = stat.grade.toString()
            textMissed.text = stat.missed.toString()
        }

        // Обработчики нажатий на кнопки предметов
        btnOOP.setOnClickListener { selectSubject(0) }
        btnMath.setOnClickListener { selectSubject(1) }
        btnDiff.setOnClickListener { selectSubject(2) }

        // По умолчанию выбран первый предмет
        selectSubject(0)

        // Обработка нижнего меню
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    Toast.makeText(this, "Главная", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Профиль", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    data class SubjectStats(
        val forecastTitle: String,
        val forecastText: String,
        val grade: Double,
        val missed: Int
    )
}