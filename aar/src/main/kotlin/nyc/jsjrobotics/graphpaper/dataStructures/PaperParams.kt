package nyc.jsjrobotics.graphpaper.dataStructures

import android.graphics.Paint

sealed class PaperParams(val drawAllEdges: Boolean = false,
                         val drawDots: Boolean = false,
                         val handleEventHistory: Boolean = false,
                         var horizontalSpacing: Int,
                         var verticalSpacing: Int,
                         var horizontalDotsSpan: Int,
                         var verticalDotsSpan: Int) {
    var pathPaint: Paint = Paint().apply {
        color = 0xFF000000.toInt()
    }

    var dotPaint: Paint = Paint().apply {
        color = 0xFF000000.toInt()
    }

    abstract fun calculateSpacing(width: Int, height: Int)

    class RelativeSpacingParam(horizontalSpacing: Int = 100,
                               verticalSpacing: Int = 100,
                               horizontalDotsSpan: Int = 10,
                               verticalDotsSpan: Int = 10) : PaperParams(horizontalSpacing = horizontalSpacing,
                                                                         verticalSpacing = verticalSpacing,
                                                                         horizontalDotsSpan = horizontalDotsSpan,
                                                                         verticalDotsSpan = verticalDotsSpan) {
        var coverBackground: Boolean = true

       override fun calculateSpacing(width: Int, height: Int) {
            horizontalSpacing = width / horizontalDotsSpan
            verticalSpacing = height / verticalDotsSpan

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

    class AbsoluteSpacingParam(horizontalSpacing: Int = 100,
                               verticalSpacing: Int = 100,
                               horizontalDotsSpan: Int = 10,
                               verticalDotsSpan: Int = 10) : PaperParams(horizontalSpacing = horizontalSpacing,
                                                                         verticalSpacing = verticalSpacing,
                                                                         horizontalDotsSpan = horizontalDotsSpan,
                                                                         verticalDotsSpan = verticalDotsSpan) {
        var coverBackground: Boolean = true

        override fun calculateSpacing(width: Int, height: Int) {
            horizontalDotsSpan = width / horizontalSpacing
            verticalDotsSpan = height / verticalSpacing

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
}