package nyc.jsjrobotics.graphpaper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import nyc.jsjrobotics.graphpaper.graphPointTree.GraphPointNode
import nyc.jsjrobotics.graphpaper.graphPointTree.GraphSetup

// A class sets its size based on parent size
class GraphPaper
@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        val drawAllEdges: Boolean = false) : View(context, attrs, defStyleAttr) {

    private val horizontalDotsSpan = 10
    private val verticalDotsSpan = 10
    private var horizontalSpacing: Int = 0
    private var verticalSpacing: Int = 0
    private var currentDrawFrom: GraphPoint? = null
    private val topLeft: GraphPointNode = GraphPointNode()
    private val dotPaint = Paint()
    private val pathPaint = Paint()

    init {
        dotPaint.color = 0xFFFF8833.toInt()
        pathPaint.color = 0xFF000000.toInt();
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        horizontalSpacing = width / horizontalDotsSpan
        verticalSpacing = height / verticalDotsSpan
        topLeft.value = GraphPoint(0f, 0f)
        topLeft.buildVerticalNodes(verticalDotsSpan, verticalSpacing)
        topLeft.buildHorizontalNodes(horizontalDotsSpan, horizontalSpacing)
        val nodeList: List<GraphPointNode> = topLeft.getSouthernEdges()
        nodeList.forEach{
            it.buildHorizontalNodes(horizontalDotsSpan, horizontalSpacing)
        }
        topLeft.getSouthernEdges(true).forEach { GraphSetup.initNorthSouthEdges(it) }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val graphPointDistance: GraphPointDistance = findStartPoint(x, y)
                startDrawTo(graphPointDistance, event)
                invalidate()

            }
            MotionEvent.ACTION_MOVE -> {
                updateDrawTo(event)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                endDrawTo(event)
                invalidate()
            }
        }
        return true
    }

    private fun endDrawTo(event: MotionEvent) {
        currentDrawFrom?.endDrawEvent(event)
    }

    private fun updateDrawTo(event: MotionEvent) {
        currentDrawFrom?.updateDrawEvent(event)
    }

    private fun startDrawTo(startIndex: GraphPointDistance, event: MotionEvent) {
        currentDrawFrom = startIndex.graphPoint
        currentDrawFrom?.addStartEvent(event)
    }

    private fun findStartPoint(x: Float, y: Float): GraphPointDistance {
        val node: GraphPointNode = topLeft.getClosestNode(x, y)
        return GraphPointDistance(Pair(node.value!!, node.calculateDistance(x, y)))
    }

    override fun onDraw(canvas: Canvas) {
        val result: ArrayList<GraphPointNode> = arrayListOf()
        topLeft.traverseEastSouth(result)
        result.forEach{
            it.value?.draw(canvas, dotPaint, pathPaint)
            if (drawAllEdges) {
                it.drawAllEdges(canvas, pathPaint)
            }
        }
    }
}

