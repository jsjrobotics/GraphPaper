package nyc.jsjrobotics.graphpaper

import android.view.MotionEvent
import nyc.jsjrobotics.graphpaper.graphPointTree.GraphPointNode

class GraphPaperDrawing(startEvent: MotionEvent,
                        val topLeft: GraphPointNode) {

    var currentNode: GraphPointNode;
    val startGraphPoint: GraphPoint;
    var endGraphPoint: GraphPoint? = null
    var finalPath: List<GraphPoint>? = null

    init {
        startGraphPoint = GraphPoint(startEvent.x, startEvent.y)
        currentNode = findStartPoint(topLeft, startGraphPoint)
        currentNode.addStartEvent()
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