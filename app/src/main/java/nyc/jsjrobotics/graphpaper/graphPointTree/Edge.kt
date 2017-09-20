package nyc.jsjrobotics.graphpaper.graphPointTree

class Edge<T>(val start: T,
              val end: T?,
              val direction: Direction) {

    fun isEmpty(): Boolean {
        return end == null
    }

    companion object {
        fun <T> empty(start: T, direction: Direction) : Edge<T> {
            return Edge(start, null, direction)
        }
    }

}