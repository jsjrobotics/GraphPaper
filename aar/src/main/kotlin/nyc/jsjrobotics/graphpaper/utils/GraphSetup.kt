package nyc.jsjrobotics.graphpaper.utils

import nyc.jsjrobotics.graphpaper.graphPointTree.GraphPointNode

class GraphSetup {
    companion object {
        fun setNorthSouth(northern: List<GraphPointNode>, southern: List<GraphPointNode>) {
            if (northern.size != southern.size) {
                throw IllegalArgumentException("Non square graph: ${northern.size} != ${southern.size}")
            }
            for (index in 0 .. northern.size-1) {
                northern[index].setSouth(southern[index])
                southern[index].setNorth(northern[index])
            }
        }

        fun setSouthEastNorthWest(northern: List<GraphPointNode>, southern: List<GraphPointNode>) {
            if (northern.size != southern.size) {
                throw IllegalArgumentException("Non square graph: ${northern.size} != ${southern.size}")
            }
            for (index in 0 .. northern.size-1) {
                if (index+1 < southern.size) {
                    northern[index].setSouthEast(southern[index+1])
                    southern[index+1].setNorthWest(northern[index])

                }
                if (index-1 >= 0) {
                    northern[index].setSouthWest(southern[index-1])
                    southern[index-1].setNorthEast(northern[index])
                }
                southern[index].setNorth(northern[index])
            }
        }

        fun initNorthSouthEdges(topLeft: GraphPointNode) {
            var currentNode = topLeft
            do {
                if (!currentNode.south.isDeadEnd()) {
                    val secondRow = topLeft.south.end!!
                    setNorthSouth(topLeft.getEasternEdges(), secondRow.getEasternEdges())
                    setSouthEastNorthWest(topLeft.getEasternEdges(true), secondRow.getEasternEdges(true))
                    currentNode = currentNode.south.end!!
                }
            } while (!currentNode.south.isDeadEnd())
        }

    }
}