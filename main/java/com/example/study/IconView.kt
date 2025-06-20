package com.example.study.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class IconView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val greenPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#36B06C")
        style = Paint.Style.FILL
    }
    private val greenStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#178B4B")
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }
    private val grayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CED4D1")
        style = Paint.Style.FILL
    }
    private val lightGreenPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#7AC3A2")
        style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val padding = w * 0.05f

        // Зеленый квадрат с закруглениями
        val rect = RectF(padding, padding, w - padding, h - padding)
        canvas.drawRoundRect(rect, w * 0.1f, w * 0.1f, greenPaint)
        canvas.drawRoundRect(rect, w * 0.1f, w * 0.1f, greenStrokePaint)

        // Серый квадрат внутри
        val innerSize = w * 0.45f
        val innerLeft = w / 2 - innerSize / 2
        val innerTop = h * 0.18f
        canvas.drawRect(innerLeft, innerTop, innerLeft + innerSize, innerTop + innerSize, grayPaint)

        // Светло-зеленая "подушка" снизу
        val pillowWidth = w * 0.7f
        val pillowHeight = h * 0.18f
        val pillowLeft = w / 2 - pillowWidth / 2
        val pillowTop = h * 0.68f
        val pillowRect = RectF(pillowLeft, pillowTop, pillowLeft + pillowWidth, pillowTop + pillowHeight)
        canvas.drawRoundRect(pillowRect, pillowHeight / 2, pillowHeight / 2, lightGreenPaint)
    }
}