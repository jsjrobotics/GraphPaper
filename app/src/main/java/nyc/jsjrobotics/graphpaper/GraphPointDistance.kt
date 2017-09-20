package nyc.jsjrobotics.graphpaper

data class GraphPointDistance(val graphPoint: GraphPoint?,
                              val distance: Double) {
    constructor(pair: Pair<GraphPoint, Double>) : this(pair.first, pair.second)

}