package nyc.jsjrobotics.graphpaper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.view.*
import nyc.jsjrobotics.graphpaper.dataStructures.PaperParams
import nyc.jsjrobotics.graphpaper.graphPointTree.GraphPointNode
import nyc.jsjrobotics.graphpaper.utils.GraphSetup
import nyc.jsjrobotics.graphpaper.utils.buildHorizontalNodes
import nyc.jsjrobotics.graphpaper.utils.buildVerticalNodes
import nyc.jsjrobotics.graphpaper.utils.getSouthernEdges
import java.util.function.Consumer


/**
 * sets its size based on parent size
 * All params can be set by
 */
class GraphPaper
@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent) {
            val dialogView = ChangeGraphPaperParamsView(context)
            val dialog = AlertDialog.Builder(context).setView(dialogView.root).create()
            dialog.show()
        }
    })

    var params: PaperParams = PaperParams.RelativeSpacingParam()
        set(value) {
            field = value
            drawAllEdges = params.drawAllEdges
            drawDots = params.drawDots
            requestLayout()
        }

    var drawAllEdges: Boolean = params.drawAllEdges
        set(value) {
            field = value
            invalidate()
        }

    var drawDots: Boolean = params.drawDots
        set(value) {
            field = value
            invalidate()
        }

    private var handleEventHistory: Boolean = params.handleEventHistory
    private var currentDrawing: GraphPaperDrawing? = null
    private val topLeft: GraphPointNode = GraphPointNode()
    private var onDrawCallback: Consumer<GraphPaperDrawing>? = null

    init {
        topLeft.value = GraphPoint(0f, 0f)
    }

    fun setOnPathCallback(onDrawComplete: Consumer<GraphPaperDrawing>?) {
        onDrawCallback = onDrawComplete
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        params.calculateSpacing(width, height)
        val verticalDotsSpan = params.verticalDotsSpan
        val verticalSpacing = params.verticalSpacing
        val horizontalDotsSpan = params.horizontalDotsSpan
        val horizontalSpacing = params.horizontalSpacing
        topLeft.setBounds(horizontalSpacing, verticalSpacing)
        topLeft.buildVerticalNodes(verticalDotsSpan, verticalSpacing)
        topLeft.buildHorizontalNodes(horizontalDotsSpan, horizontalSpacing)
        val nodeList: List<GraphPointNode> = topLeft.getSouthernEdges()
        nodeList.forEach{
            it.buildHorizontalNodes(horizontalDotsSpan, horizontalSpacing)
        }
        topLeft.getSouthernEdges(true).forEach { GraphSetup.initNorthSouthEdges(it) }

    }

    private var lastPath: Path? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val gestureDetected = gestureDetector.onTouchEvent(event)
        if (gestureDetected) {
            return true
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)

                startDrawTo(event)
                invalidate()

            }
            MotionEvent.ACTION_MOVE -> {
                updateDrawTo(event)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)
                val drawing: GraphPaperDrawing = endDrawTo(event)
                onDrawCallback?.accept(drawing)
                invalidate()
            }
        }
        return true
    }

    private fun endDrawTo(event: MotionEvent) : GraphPaperDrawing {
        currentDrawing!!.endDrawEvent(event, mutableListOf())
        val finalDrawing = currentDrawing!!
        currentDrawing = null
        if (handleEventHistory) {
            topLeft.clearDrawing(arrayListOf())
        }
        return finalDrawing

    }

    private fun updateDrawTo(event: MotionEvent) {
        if (handleEventHistory) {
            handleEventHistory(event)
        }
        currentDrawing!!.updateDrawTo(event)
    }

    private fun handleEventHistory(event: MotionEvent) {
        val historySize = event.historySize
        for (index in 0..historySize - 1) {
            val x = event.getHistoricalX(index)
            val y = event.getHistoricalY(index)
            val eventTime = event.getHistoricalEventTime(index)
            val action = event.action
            val oldEvent: MotionEvent = MotionEvent.obtain(-1, eventTime, action, x, y, event.metaState)
            currentDrawing!!.updateDrawTo(oldEvent)
            oldEvent.recycle()
        }
    }

    private fun startDrawTo(event: MotionEvent) {
        currentDrawing = GraphPaperDrawing(event, topLeft)
    }

    fun setPathPaint(paint: Paint) {
        params.pathPaint = paint
    }

    fun setDotPaint(paint: Paint) {
        params.dotPaint = paint
    }

    override fun onDraw(canvas: Canvas) {
        val result: MutableList<GraphPointNode> = arrayListOf()
        val dotPaint = if (drawDots) params.dotPaint else null
        val pathPaint = params.pathPaint
        topLeft.traverseEastSouth(result)
        result.forEach{
            it.value?.draw(canvas, dotPaint, pathPaint)
            if (drawAllEdges) {
                it.drawAllEdges(canvas, pathPaint)
            }
        }
        if (lastPath != null) {
            canvas.drawPath(lastPath, pathPaint)
        }
    }
}