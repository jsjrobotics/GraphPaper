package nyc.jsjrobotics

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import nyc.jsjrobotics.graphpaper.GraphPaper
import nyc.jsjrobotics.graphpaper.GraphPaperDrawing
import nyc.jsjrobotics.graphpaper.dataStructures.PaperParams
import java.util.function.Consumer

class MainActivity : AppCompatActivity() {

    val useRelativeSpacing: Boolean = false
    private lateinit var view: GraphPaper

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            view = GraphPaper(this)
            view.params = buildGraphParams()
            view.setOnPathCallback(buildPathCallback())
            setContentView(view)
        }

        private fun buildGraphParams(): PaperParams.RelativeSpacingParam {
            return PaperParams.RelativeSpacingParam().apply {
                    horizontalDotsSpan = 8
                    verticalDotsSpan = 8
            }
        }

        private fun buildPathCallback(): Consumer<GraphPaperDrawing> {
            return Consumer {
                Log.d("GridPointsTouched", it.finalPath.toString())
            }
    }
}