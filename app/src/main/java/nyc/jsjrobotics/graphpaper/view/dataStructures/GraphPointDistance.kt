package nyc.jsjrobotics.graphpaper.view.dataStructures

import nyc.jsjrobotics.graphpaper.view.GraphPoint

data class GraphPointDistance(val graphPoint: GraphPoint?,
                              val distance: Double) {
    constructor(pair: Pair<GraphPoint, Double>) : this(pair.first, pair.second)

}