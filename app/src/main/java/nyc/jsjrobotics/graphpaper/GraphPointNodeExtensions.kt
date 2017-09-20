package nyc.jsjrobotics.graphpaper

import nyc.jsjrobotics.graphpaper.graphPointTree.Edge
import nyc.jsjrobotics.graphpaper.graphPointTree.GraphPointNode
import java.util.function.Function


fun GraphPointNode.buildVerticalNodes(verticalDotsSpan: Int, verticalSpacing: Int) {
    val verticalNodes: ArrayList<GraphPointNode> = arrayListOf(this)
    for (yIndex in 1..verticalDotsSpan+1) {
        val xPosition = this.value?.x ?: 0f
        val yPosition = yIndex * verticalSpacing
        val value: GraphPoint = GraphPoint(xPosition, yPosition.toFloat())
        val newNode: GraphPointNode = GraphPointNode(value)
        val northNode: GraphPointNode = verticalNodes[yIndex-1]
        northNode.setSouth(newNode)
        newNode.setNorth(northNode)
        verticalNodes.add(newNode)
    }
}
fun GraphPointNode.buildHorizontalNodes(horizontalDotsSpan: Int, horizontalSpacing: Int) {
    val horizontalNodes: ArrayList<GraphPointNode> = arrayListOf(this)
    for (xIndex in 1..horizontalDotsSpan) {
        val xPosition = xIndex * horizontalSpacing
        val yPosition: Float = this.value?.y ?: 0f
        val value: GraphPoint = GraphPoint(xPosition.toFloat(), yPosition)
        val newNode: GraphPointNode = GraphPointNode(value)
        val westNode: GraphPointNode = horizontalNodes[xIndex-1]
        westNode.setEast(newNode)
        newNode.setWest(westNode)
        horizontalNodes.add(newNode)
    }
}
fun GraphPointNode.getNorthernEdges(inclusive: Boolean = false): List<GraphPointNode> {
    return getEdges(this, Function{ it.north}, inclusive)
}

fun GraphPointNode.getEasternEdges(inclusive: Boolean = false): List<GraphPointNode> {
    return getEdges(this, Function{ it.east}, inclusive)
}

fun GraphPointNode.getWesternEdges(inclusive: Boolean = false): List<GraphPointNode> {
    return getEdges(this, Function{ it.west}, inclusive)
}

fun GraphPointNode.getSouthernEdges(inclusive: Boolean = false): List<GraphPointNode> {
    return getEdges(this, Function{ it.south}, inclusive)
}

fun GraphPointNode.getSouth(steps: Int): GraphPointNode? {
    return getStepsAway(steps, Function{ it.getSouthernEdges()})
}

fun GraphPointNode.getNorth(steps: Int): GraphPointNode? {
    return getStepsAway(steps, Function{ it.getNorthernEdges()})
}

fun GraphPointNode.getEast(steps: Int): GraphPointNode? {
    return getStepsAway(steps, Function{ it.getEasternEdges()})
}

fun GraphPointNode.getWest(steps: Int): GraphPointNode? {
    return getStepsAway(steps, Function{ it.getWesternEdges()})
}

private fun GraphPointNode.getStepsAway(steps: Int, toEdges: Function<GraphPointNode, List<GraphPointNode>>): GraphPointNode? {
    if (steps < 0) {
        throw IllegalArgumentException("Can't go negative steps")
    }
    if (steps == 0) {
        return this
    }
    val edges = toEdges.apply(this)
    if (edges.size <= steps) {
        return null
    }
    return edges[steps-1]
}

private fun GraphPointNode.getEdges(topLeft: GraphPointNode,
                     toEdge: Function<GraphPointNode, Edge<GraphPointNode?>>,
                     inclusive: Boolean = false): List<GraphPointNode> {
    val nodes: ArrayList<GraphPointNode> = arrayListOf()
    var currentNode: GraphPointNode? = topLeft
    do {
        if (currentNode != null){
            val currentEdge = toEdge.apply(currentNode)
            if (!currentEdge.isDeadEnd()) {
                nodes.add(currentEdge.end!!)
            }
            currentNode = currentEdge.end
        }
    } while (currentNode != null)
    if (inclusive) {
        val result: ArrayList<GraphPointNode> = arrayListOf(this)
        result.addAll(nodes)
        return result
    }
    return nodes
}