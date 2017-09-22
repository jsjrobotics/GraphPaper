package nyc.jsjrobotics.graphpaper

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import nyc.jsjrobotics.graphpaper.view.GraphPaper
import nyc.jsjrobotics.graphpaper.view.GraphPaperDrawing
import nyc.jsjrobotics.graphpaper.view.dataStructures.GraphPaperParams
import java.util.function.Consumer

class MainActivity : AppCompatActivity() {

    val useRelativeSpacing: Boolean = false
    private lateinit var view: GraphPaper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = GraphPaper(this)
        view.params = GraphPaperParams(useRelativeSpacing = useRelativeSpacing)
        view.setOnPathCallback(buildPathCallback())
        setContentView(view)
    }

    private fun buildPathCallback(): Consumer<GraphPaperDrawing> {
        return Consumer {
            Log.d("GridPointsTouched", it.finalPath.toString())
        }
    }
}