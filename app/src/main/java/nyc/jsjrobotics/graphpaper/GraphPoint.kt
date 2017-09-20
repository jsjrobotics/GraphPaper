package nyc.jsjrobotics.graphpaper

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.view.MotionEvent

data class GraphPoint(val x: Float = 0f,
                 val y: Float = 0f) {
    private val dotRadius: Int = 5

    private val mDrawTo: ArrayList<GraphPoint> = arrayListOf()

    fun addStartEvent(event: MotionEvent) {
        mDrawTo.add(GraphPoint(event.x, event.y))
    }

    fun updateDrawEvent(event: MotionEvent) {
        mDrawTo.clear()
        mDrawTo.add(GraphPoint(event.x, event.y))
    }

    fun endDrawEvent(event: MotionEvent) {
        mDrawTo.clear()
    }

    fun draw(canvas: Canvas, dotPaint: Paint, pathPaint: Paint) {
        canvas.drawCircle(x, y, dotRadius.toFloat(), dotPaint)
        mDrawTo.forEach{ canvas.drawLine(x, y, it.x, it.y, pathPaint)}
    }

}