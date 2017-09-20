package nyc.jsjrobotics.graphpaper.graphPointTree

import nyc.jsjrobotics.graphpaper.getEasternEdges

class GraphSetup {
    companion object {
        fun setNorthSouth(northern: List<GraphPointNode>, southern: List<GraphPointNode>) {
            if (northern.size != southern.size) {
                throw IllegalArgumentException("Non square graph")
            }
            for (index in 0 .. northern.size-1) {
                northern[index].setSouth(southern[index])
                southern[index].setNorth(northern[index])
            }
        }

        fun initNorthSouthEdges(topLeft: GraphPointNode) {
            var currentNode = topLeft
            do {
                if (!currentNode.south.isEmpty()) {
                    val secondRow = topLeft.south.end!!
                    setNorthSouth(topLeft.getEasternEdges(), secondRow.getEasternEdges())
                    currentNode = currentNode.south.end!!
                }
            } while (!currentNode.south.isEmpty())
        }

    }
}