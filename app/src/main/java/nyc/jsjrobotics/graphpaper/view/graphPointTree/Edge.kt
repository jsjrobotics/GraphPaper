package nyc.jsjrobotics.graphpaper.view.graphPointTree

class Edge<T>(val start: T,
              val end: T?,
              val direction: Direction) {

    fun isDeadEnd(): Boolean {
        return end == null
    }

    companion object {
        fun <T> empty(start: T, direction: Direction) : Edge<T> {
            return Edge(start, null, direction)
        }
    }

}