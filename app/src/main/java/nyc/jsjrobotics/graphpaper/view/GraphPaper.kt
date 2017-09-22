package nyc.jsjrobotics.graphpaper.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import nyc.jsjrobotics.graphpaper.view.dataStructures.GraphPaperParams
import nyc.jsjrobotics.graphpaper.view.graphPointTree.GraphPointNode
import nyc.jsjrobotics.graphpaper.view.utils.GraphSetup
import nyc.jsjrobotics.graphpaper.view.utils.buildHorizontalNodes
import nyc.jsjrobotics.graphpaper.view.utils.buildVerticalNodes
import nyc.jsjrobotics.graphpaper.view.utils.getSouthernEdges
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

    var params: GraphPaperParams = GraphPaperParams()
        set(value) {
            field = value
            requestLayout()
        }

    private var drawAllEdges: Boolean = params.drawAllEdges
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
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startDrawTo(event)
                invalidate()

            }
            MotionEvent.ACTION_MOVE -> {
                updateDrawTo(event)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
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


    override fun onDraw(canvas: Canvas) {
        val result: MutableList<GraphPointNode> = arrayListOf()
        val dotPaint = params.dotPaint
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

class GraphPaperDrawing(startEvent: MotionEvent,
                        val topLeft: GraphPointNode) {

    var currentNode: GraphPointNode;
    val startGraphPoint: GraphPoint;
    var endGraphPoint: GraphPoint? = null
    var finalPath: List<GraphPoint>? = null

    init {
        startGraphPoint = GraphPoint(startEvent.x, startEvent.y)
        currentNode = findStartPoint(topLeft, startGraphPoint)
        currentNode.addStartEvent(startEvent)
    }

    fun updateDrawTo(event: MotionEvent) {
        currentNode = currentNode.updateDrawTo(event)
    }

    fun endDrawEvent(event: MotionEvent, previousPath: MutableList<GraphPoint>?) {
        finalPath = currentNode.endDrawEvent(event, previousPath).reversed()
        endGraphPoint = GraphPoint(event.x, event.y)
    }

    companion object {
        private fun findStartPoint(startNode: GraphPointNode, point: GraphPoint): GraphPointNode {
            return startNode.getClosestNode(point.x, point.y)
        }
    }

}

