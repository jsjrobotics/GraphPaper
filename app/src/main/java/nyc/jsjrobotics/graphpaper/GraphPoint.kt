package nyc.jsjrobotics.graphpaper

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent

data class GraphPoint(val x: Float = 0f,
                 val y: Float = 0f) {
    private val dotRadius: Int = 5
    private val dotPaint = Paint()
    init {
        dotPaint.color = 0xFFFF8833.toInt()
    }

    fun addStartEvent(event: MotionEvent) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun updateDrawEvent(event: MotionEvent) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun endDrawEvent(event: MotionEvent) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun draw(canvas: Canvas, horizontalSpacing: Int, verticalSpacing: Int) {
        canvas.drawCircle(x, y, dotRadius.toFloat(), dotPaint)
    }

    fun draw(canvas: Canvas, horizontalSpacing: Int, verticalSpacing: Int, paint: Paint) {
        canvas.drawCircle(x, y, dotRadius.toFloat(), paint)
    }

}