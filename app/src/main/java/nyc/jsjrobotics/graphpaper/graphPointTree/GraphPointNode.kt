package nyc.jsjrobotics.graphpaper.graphPointTree

import android.graphics.Canvas
import android.graphics.Paint
import nyc.jsjrobotics.graphpaper.GraphPoint


class GraphPointNode(var value: GraphPoint? = null,
                     north: GraphPointNode? = null,
                     south: GraphPointNode? = null,
                     east: GraphPointNode? = null,
                     west: GraphPointNode? = null,
                     northEast: GraphPointNode? = null,
                     southEast: GraphPointNode? = null,
                     northWest: GraphPointNode? = null,
                     southWest: GraphPointNode? = null) {

    var north: Edge<GraphPointNode?> = Edge.empty(this, Direction.NORTH)
    var south: Edge<GraphPointNode?> = Edge.empty(this, Direction.SOUTH)
    var east: Edge<GraphPointNode?> = Edge.empty(this, Direction.EAST)
    var west: Edge<GraphPointNode?> = Edge.empty(this, Direction.WEST)

    var northWest: Edge<GraphPointNode?> = Edge.empty(this, Direction.NORTH_WEST)
    var southWest: Edge<GraphPointNode?> = Edge.empty(this, Direction.SOUTH_WEST)
    var northEast: Edge<GraphPointNode?> = Edge.empty(this, Direction.NORTH_EAST)
    var southEast: Edge<GraphPointNode?> = Edge.empty(this, Direction.SOUTH_EAST)

    fun traverseEastSouth(resultList: ArrayList<GraphPointNode>) {
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

    private fun edges() : List<Edge<GraphPointNode?>>{
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
        edges().filter { !it.isDeadEnd()}
                .map { it.end!! }
                .filter { it.value != null }
                .map { it.value!! }
                .forEach {
                    val neighborX = it.x
                    val neighborY = it.y
                    canvas.drawLine(value!!.x, value!!.y, neighborX, neighborY, pathPaint )
                }
    }


}