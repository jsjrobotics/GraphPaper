package nyc.jsjrobotics.graphpaper

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import nyc.jsjrobotics.graphpaper.view.GraphPaper
import nyc.jsjrobotics.graphpaper.view.GraphPaperDrawing
import java.util.function.Consumer

class MainActivity : AppCompatActivity() {
    private lateinit var view: GraphPaper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = GraphPaper(this)
        view.setOnPathCallback(buildPathCallback())
        setContentView(view)
    }

    private fun buildPathCallback(): Consumer<GraphPaperDrawing> {
        return Consumer {
            Log.d("GridPointsTouched", it.finalPath.toString())
        }
    }
}