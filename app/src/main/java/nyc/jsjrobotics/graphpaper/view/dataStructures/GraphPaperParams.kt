package nyc.jsjrobotics.graphpaper.view.dataStructures

import android.graphics.Paint

/***
 * Class to initalize graph paper.
 * If use relative spacing is true, horizontalFixedSpacing and verticalFixedSpacing are ignored and
 * dot width = view width / horizontalDotsSpan.
 *
 * If use relative spacing is false, horizontalDotsSpan and verticalDotsSpan are ignored and dot width
 * is set by the spacing params in pixels
 *
 * Set coverBackground to true to guarantee the entire view is covered with graph dots
 */
class GraphPaperParams(var useRelativeSpacing: Boolean = false,
                       horizontalSpacing: Int = 100,
                       verticalSpacing: Int = 100,
                       horizontalDotsSpan: Int = 10,
                       verticalDotsSpan: Int = 10,
                       var coverBackground: Boolean = true,
                       var drawAllEdges: Boolean = false,
                       var handleEventHistory: Boolean = false,
                       dotPaintColor: Int = 0xFFFF8833.toInt(),
                       paintPathColor: Int = 0xFF000000.toInt()) {
    val dotPaint = Paint()
    val pathPaint = Paint()
    var horizontalSpacing: Int private set
    var verticalSpacing: Int private set
    var horizontalDotsSpan: Int private set
    var verticalDotsSpan: Int private set

    init {
        dotPaint.color = dotPaintColor
        pathPaint.color = paintPathColor
        this.horizontalSpacing = horizontalSpacing
        this.verticalSpacing = verticalSpacing
        this.horizontalDotsSpan = horizontalDotsSpan
        this.verticalDotsSpan = verticalDotsSpan
    }

    fun calculateSpacing(width: Int, height: Int) {
        if (useRelativeSpacing) {
            horizontalSpacing = width / horizontalDotsSpan
            verticalSpacing = height / verticalDotsSpan
        } else {
            horizontalDotsSpan = width / horizontalSpacing
            verticalDotsSpan = height / verticalSpacing
        }
        if (coverBackground) {
            while (horizontalSpacing * horizontalDotsSpan < width) {
                horizontalDotsSpan += 1
            }
            while (verticalSpacing * verticalDotsSpan < height) {
                verticalDotsSpan += 1
            }
        }
    }


}