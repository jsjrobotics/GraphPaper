package nyc.jsjrobotics.graphpaper

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent

data class GraphPoint(val x: Float = 0f,
                 val y: Float = 0f) {
    private val dotRadius: Int = 5

    private val mDrawTo: MutableList<GraphPoint> = mutableListOf()

    fun addStartEvent() {
        // Note we add the start event coordinates for this graph point not the event
        mDrawTo.add(GraphPoint(x, y))
    }

    fun updateDrawEvent(event: MotionEvent) {
        updateDrawEvent(event.x, event.y)
    }

    fun updateDrawEvent(x: Float, y: Float) {
        mDrawTo.removeAt(mDrawTo.lastIndex)
        mDrawTo.add(GraphPoint(x, y))
    }

    fun endDrawEvent(event: MotionEvent, path: MutableList<GraphPoint>) {
        // Note we add the end event coordinates for this graph point not the event
        updateDrawEvent(x,y)
        mDrawTo.forEach {
            path.add(GraphPoint(it.x, it.y))
        }
        mDrawTo.clear()
    }

    fun isDrawing(): Boolean {
        return mDrawTo.isNotEmpty()
    }

    fun draw(canvas: Canvas,
             dotPaint: Paint?,
             pathPaint: Paint?) {
        if (dotPaint != null) {
            canvas.drawCircle(x, y, dotRadius.toFloat(), dotPaint)
        }
        if (pathPaint != null) {
            mDrawTo.forEach { canvas.drawLine(x, y, it.x, it.y, pathPaint) }
        }
    }

    fun clearDrawing() {
        mDrawTo.clear()
    }

}