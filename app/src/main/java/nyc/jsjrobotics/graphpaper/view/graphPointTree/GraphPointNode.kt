package nyc.jsjrobotics.graphpaper.view.graphPointTree

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import nyc.jsjrobotics.graphpaper.view.GraphPoint


class GraphPointNode(var value: GraphPoint? = null,
                     var boundsWidth: Int = 0,
                     var boundsHeight: Int = 0,
                     north: GraphPointNode? = null,
                     south: GraphPointNode? = null,
                     east: GraphPointNode? = null,
                     west: GraphPointNode? = null,
                     northEast: GraphPointNode? = null,
                     southEast: GraphPointNode? = null,
                     northWest: GraphPointNode? = null,
                     southWest: GraphPointNode? = null) {
    private var previousNodes: MutableList<GraphPointNode> = arrayListOf()
    var north: Edge<GraphPointNode?> = Edge.empty(this, Direction.NORTH)
    var south: Edge<GraphPointNode?> = Edge.empty(this, Direction.SOUTH)
    var east: Edge<GraphPointNode?> = Edge.empty(this, Direction.EAST)
    var west: Edge<GraphPointNode?> = Edge.empty(this, Direction.WEST)

    var northWest: Edge<GraphPointNode?> = Edge.empty(this, Direction.NORTH_WEST)
    var southWest: Edge<GraphPointNode?> = Edge.empty(this, Direction.SOUTH_WEST)
    var northEast: Edge<GraphPointNode?> = Edge.empty(this, Direction.NORTH_EAST)
    var southEast: Edge<GraphPointNode?> = Edge.empty(this, Direction.SOUTH_EAST)

    fun traverseEastSouth(resultList: MutableList<GraphPointNode>) {
        if (resultList.contains(this)) {
            return
        }
        resultList.add(this)
        east.end?.traverseEastSouth(resultList)
        south.end?.traverseEastSouth(resultList)
        west.end?.traverseEastSouth(resultList)
        north.end?.traverseEastSouth(resultList)
    }

    init {
        setNorth(north)
        setSouth(south)
        setEast(east)
        setWest(west)
        setNorthWest(northWest)
        setSouthWest(southWest)
        setNorthEast(northEast)
        setSouthEast(southEast)
    }

    fun setNorth(north: GraphPointNode?){
        this.north = Edge(this, north, Direction.NORTH)
    }
    fun setSouth(south: GraphPointNode?){
        this.south = Edge(this, south, Direction.SOUTH)
    }
    fun setEast(east: GraphPointNode?) {
        this.east = Edge(this, east, Direction.EAST)
    }
    fun setWest(west: GraphPointNode?) {
        this.west = Edge(this, west, Direction.WEST)
    }

    fun setNorthEast(node: GraphPointNode?) {
        northEast = Edge(this, node, Direction.NORTH_EAST)
    }

    fun setSouthEast(node: GraphPointNode?) {
        southEast = Edge(this, node, Direction.SOUTH_EAST)
    }
    fun setNorthWest(node: GraphPointNode?) {
        northWest = Edge(this, node, Direction.NORTH_WEST)
    }
    fun setSouthWest(node: GraphPointNode?) {
        southWest = Edge(this, node, Direction.SOUTH_WEST)
    }

    fun getClosestNode(x: Float, y: Float): GraphPointNode {
        return compareClosestNode(x, y, this)
    }

    private fun compareClosestNode(x: Float, y: Float, closestNode: GraphPointNode): GraphPointNode {
        var closer = closestNode
        var closestDistance = closer.calculateDistance(x, y)
        var edgesToCheck: HashSet<Edge<GraphPointNode?>> = hashSetOf()
        val checkedEdges: HashSet<Edge<GraphPointNode?>> = hashSetOf()
        edgesToCheck.addAll(closer.edges())
        do {
            val nextEdges: HashSet<Edge<GraphPointNode?>> = hashSetOf()
            edgesToCheck
                    .filter { !it.isDeadEnd() }
                    .map {
                        if (!checkedEdges.contains(it)) {
                            checkedEdges.add(it)
                        }
                        it.end!!
                    }
                    .forEach {
                        it.edges().forEach {
                            if (!checkedEdges.contains(it)) {
                                nextEdges.add(it)
                            }
                        }
                        val distance = it.calculateDistance(x,y)
                        if (distance < closestDistance ) {
                            closer = it
                            closestDistance = distance
                        }
                    }
            edgesToCheck = nextEdges
        } while (edgesToCheck.isNotEmpty())
        return closer
    }

    fun edges() : List<Edge<GraphPointNode?>>{
        return listOf(
                north,
                northEast,
                east,
                southEast,
                south,
                southWest,
                west,
                northWest
        )
    }

    fun calculateDistance(x: Float, y: Float): Double {
        if(value == null) {
            return -1.0
        }
        val xSquared = Math.pow((value!!.x - x).toDouble(), 2.0)
        val ySquared = Math.pow((value!!.y - y).toDouble(), 2.0)
        return Math.sqrt(xSquared + ySquared)
    }

    fun drawAllEdges(canvas: Canvas, pathPaint: Paint) {
        if (value == null) {
            return
        }
        neighborValues().forEach {
                    val neighborX = it.x
                    val neighborY = it.y
                    canvas.drawLine(value!!.x, value!!.y, neighborX, neighborY, pathPaint )
                }
    }

    private fun neighbors(): List<GraphPointNode> {
        return edges().filter { !it.isDeadEnd()}
                .map { it.end!! }
    }

    private fun neighborValues(): List<GraphPoint> {
        return edges().filter { !it.isDeadEnd()}
                .map { it.end!! }
                .filter { it.value != null }
                .map { it.value!! }
    }

    fun addStartEvent(event: MotionEvent) {
        value?.addStartEvent(event)
    }

    fun addStartEvent(previousNode: GraphPointNode, event: MotionEvent) {
        previousNodes.add(previousNode)
        value?.addStartEvent(event)
    }

    fun endDrawEvent(event: MotionEvent, previousPath: MutableList<GraphPoint>?) : MutableList<GraphPoint> {
        if (!value!!.isDrawing()){
            return previousPath ?: mutableListOf()
        }
        val path: MutableList<GraphPoint>
        if (previousPath == null) {
            path = arrayListOf()
        } else {
            path = previousPath
        }
        value?.endDrawEvent(event, path)
        previousNodes.forEach { it.endDrawEvent(event, path) }
        previousNodes.clear()
        return path
    }

    fun updateDrawTo(event: MotionEvent): GraphPointNode {
        if (!insideDrawingArea(event)) {
            val neighbor: GraphPointNode = closestNeighbor(event)
            value?.updateDrawEvent(neighbor.value!!.x, neighbor.value!!.y)
            neighbor.addStartEvent(this, event)
            return neighbor
        } else {
            value?.updateDrawEvent(event)
            return this
        }
    }

    private fun closestNeighbor(event: MotionEvent): GraphPointNode {
        val toCheck = neighbors()
        return toCheck.foldRight(toCheck.get(0), getLowerDistance(event))
    }

    private fun getLowerDistance(event: MotionEvent): (GraphPointNode, GraphPointNode) -> GraphPointNode {
        return object : (GraphPointNode, GraphPointNode) -> GraphPointNode {
            override fun invoke(p1: GraphPointNode, acc: GraphPointNode): GraphPointNode {
                val p1Distance = p1.calculateDistance(event.x, event.y)
                val accDistance = acc.calculateDistance(event.x, event.y)
                if (p1Distance < accDistance) {
                    return p1
                }
                return acc
            }
        }
    }

    private fun insideDrawingArea(event: MotionEvent): Boolean {
        if (value == null) {
            throw IllegalArgumentException("No Value")
        }
        val xOrigin = value!!.x
        val yOrigin = value!!.y
        val left: Float = xOrigin - boundsWidth
        val top: Float = yOrigin + boundsHeight
        val right: Float = xOrigin + boundsWidth
        val bottom: Float = yOrigin - boundsHeight
        val horizontallyBound = event.x in left..right
        val verticallyBound = event.y in bottom..top
        val inside = horizontallyBound && verticallyBound
        return inside
    }

    fun setBounds(horizontalSpacing: Int, verticalSpacing: Int) {
        boundsWidth = horizontalSpacing
        boundsHeight = verticalSpacing
    }

    fun clearDrawing(clearedNodes: MutableList<GraphPointNode>) {
        if (!clearedNodes.contains(this)) {
            value?.clearDrawing()
            clearedNodes.add(this)
            neighbors().forEach { it.clearDrawing(clearedNodes) }
        }
    }


}