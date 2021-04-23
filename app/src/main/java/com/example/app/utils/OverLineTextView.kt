package com.example.app.utils

import android.app.Notification
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.app.R


class OverLineTextView : androidx.appcompat.widget.AppCompatTextView {
    private var paint: Paint? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    private fun init() {
        paint = Paint()
        paint?.setAntiAlias(true)
        paint?.setColor(currentTextColor)
        paint?.setStyle(Paint.Style.STROKE)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = getPaint().measureText(text.toString())
        if(paint != null){
            canvas.drawLine(
                totalPaddingLeft.toFloat(), totalPaddingTop.toFloat() + 1.toFloat(),
                totalPaddingLeft + width.toFloat(), totalPaddingTop + 1.toFloat(), paint!!
            )
        }

    }
}