package com.example.task4

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class ClockView(
    context: Context,
    private val attrs: AttributeSet
) : View(context, attrs) {

    private var currentSecond: Int = 0
    private var currentMinute: Int = 0
    private var currentHour: Int = 0

    private var secondAngle: Float = 0.0F
    private var minuteAngle: Float = 0.0F
    private var hourAngle: Float = 0.0F

    private val paintCircle: Paint = Paint()
    private val paintLine: Paint = Paint()
    private val paintCenter: Paint = Paint()
    private val paintSecond: Paint = Paint()
    private val paintMinute: Paint = Paint()
    private val paintHour: Paint = Paint()

    private val centerRadius: Float = 8.0F
    private val markLength = 30.0F
    private var centerX: Float = 0F
    private var centerY: Float = 0F
    private var radius: Float = 0F

    private var secondHandLength: Float = 0.0F
    private var minuteHandLength: Float = 0.0F
    private var hourHandLength: Float = 0.0F

    private var calendar = Calendar.getInstance()

    init {
        paintCircle.style = Paint.Style.STROKE
        paintCircle.strokeWidth = 6.0F

        paintLine.strokeWidth = 13.0F

        paintSecond.strokeWidth = 12.0F
        paintMinute.strokeWidth = 12.0F
        paintHour.strokeWidth = 12.0F

        setUpAttributes()
    }

    private fun setUpAttributes() {
        val xmlAttributes = context.theme.obtainStyledAttributes(attrs, R.styleable.ClockView, 0, 0)

        paintSecond.color = xmlAttributes.getColor(R.styleable.ClockView_secondHandColor, DEFAULT_SECOND_HAND_COLOR)
        paintMinute.color = xmlAttributes.getColor(R.styleable.ClockView_minuteHandColor, DEFAULT_MINUTE_HAND_COLOR)
        paintHour.color = xmlAttributes.getColor(R.styleable.ClockView_hourHandColor, DEFAULT_HOUR_HAND_COLOR)

        secondHandLength = xmlAttributes.getFloat(R.styleable.ClockView_secondHandSize, -123.0F)
        minuteHandLength = xmlAttributes.getFloat(R.styleable.ClockView_minuteHandSize, -123.0F)
        hourHandLength = xmlAttributes.getFloat(R.styleable.ClockView_hourHandSize, -123.0F)

        xmlAttributes.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerX = width / 2.0F
        centerY = height / 2.0F
        radius = min(width, height) * 0.45F

        if(hourHandLength == -123.0F){
            hourHandLength =  radius * 0.55F
        }
        if(minuteHandLength == -123.0F){
            minuteHandLength = radius * 0.7F
        }
        if(secondHandLength == -123.0F){
            secondHandLength = radius * 0.85F
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        updateTime()
        calculateAngles()

        if (canvas != null) {
            drawClock(canvas)
            drawSecondHand(canvas)
            drawMinuteHand(canvas)
            drawHourHand(canvas)
        }

        postInvalidateDelayed(500)
        invalidate()
    }

    private fun calculateAngles() {
        hourAngle = ((Math.PI / 6.0) * currentHour).toFloat()
        minuteAngle = ((Math.PI / 30.0) * currentMinute).toFloat()
        secondAngle = ((Math.PI / 30.0) * currentSecond).toFloat()
    }

    private fun updateTime() {
        calendar = Calendar.getInstance()
        currentSecond = calendar[Calendar.SECOND]
        currentMinute = calendar[Calendar.MINUTE]
        currentHour = calendar[Calendar.HOUR_OF_DAY]
        currentHour = if (currentHour > 12) currentHour - 12 else currentHour
    }

    private fun drawClock(canvas: Canvas) {
        drawCircle(canvas)
        drawCenter(canvas)
        drawMarks(canvas)
    }

    private fun drawMarks(canvas: Canvas) {
        (1..12).forEach { number ->
            val angle = number * Math.PI / 6.0
            val x = centerX + radius * cos(angle)
            val y = centerY + radius * sin(angle)
            canvas.drawLine(
                x.toFloat(),
                y.toFloat(),
                (x - markLength * cos(angle)).toFloat(),
                (y - markLength * sin(angle)).toFloat(),
                paintLine
            )
        }
    }

    private fun drawCircle(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, radius, paintCircle)
    }

    private fun drawCenter(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, centerRadius, paintCenter)
    }

    private fun drawHourHand(canvas: Canvas) {
        val angle = hourAngle + minuteAngle / 12.0F + secondAngle / 720.0F

        val x = centerX + hourHandLength * sin(angle)
        val y = centerY - hourHandLength * cos(angle)
        canvas.drawLine(centerX, centerY, x, y, paintHour)
    }

    private fun drawMinuteHand(canvas: Canvas) {
        val angle = minuteAngle + secondAngle / 60.0F

        val x = centerX + minuteHandLength * sin(angle)
        val y = centerY - minuteHandLength * cos(angle)
        canvas.drawLine(centerX, centerY, x, y, paintMinute)
    }

    private fun drawSecondHand(canvas: Canvas) {
        val angle = secondAngle

        val x = centerX + secondHandLength * sin(angle)
        val y = centerY - secondHandLength * cos(angle)
        canvas.drawLine(centerX, centerY, x, y, paintSecond)
    }

    companion object {
        const val DEFAULT_SECOND_HAND_COLOR = Color.RED
        const val DEFAULT_MINUTE_HAND_COLOR = Color.BLUE
        const val DEFAULT_HOUR_HAND_COLOR = Color.GREEN
    }
}