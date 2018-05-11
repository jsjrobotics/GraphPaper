package nyc.jsjrobotics

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import nyc.jsjrobotics.graphpaper.GraphPaper
import nyc.jsjrobotics.graphpaper.GraphPaperDrawing
import nyc.jsjrobotics.graphpaper.dataStructures.FloatPoint
import nyc.jsjrobotics.graphpaper.dataStructures.RelativeSpacingParam
import java.util.function.Consumer

class SpecifyPointsActivity : AppCompatActivity() {
    private val points = "67.0,42.0%202.0,42.0%337.0,42.0%472.0,42.0%607.0,42.0%742.0,42.0%877.0,42.0%1012.0,42.0%67.0,126.0%202.0,126.0%337.0,126.0%472.0,126.0%607.0,126.0%742.0,126.0%877.0,126.0%1012.0,126.0%67.0,210.0%202.0,210.0%337.0,210.0%472.0,210.0%607.0,210.0%742.0,210.0%877.0,210.0%1012.0,210.0%67.0,293.0%202.0,293.0%337.0,293.0%472.0,293.0%607.0,293.0%742.0,293.0%877.0,293.0%1012.0,293.0%67.0,377.0%202.0,377.0%337.0,377.0%472.0,377.0%607.0,377.0%742.0,377.0%877.0,377.0%1012.0,377.0%67.0,461.0%202.0,461.0%337.0,461.0%472.0,461.0%607.0,461.0%742.0,461.0%877.0,461.0%1012.0,461.0%67.0,545.0%202.0,545.0%337.0,545.0%472.0,545.0%607.0,545.0%742.0,545.0%877.0,545.0%1012.0,545.0%67.0,629.0%202.0,629.0%337.0,629.0%472.0,629.0%607.0,629.0%742.0,629.0%877.0,629.0%1012.0,629.0"
    private val pointList : List<Pair<Float,Float>> = points.split("%")
            .map {
                val args = it.split(",")
                if (args.size != 2) {
                    throw IllegalArgumentException("Unpaired points")
                }
                Pair(args[0].toFloat(), args[1].toFloat())
            }

    private lateinit var view: GraphPaper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = GraphPaper(this)
        view.params = buildGraphParams()
        view.setOnPathCallback(buildPathCallback())
        setContentView(view)
    }

    private fun buildGraphParams(): RelativeSpacingParam {
        return RelativeSpacingParam().apply {
            dotPositionsSpecified = pointList.map { FloatPoint(it.first, it.second) }
        }
    }

    private fun buildPathCallback(): Consumer<GraphPaperDrawing> {
        return Consumer {
            Log.d("GridPointsTouched", it.finalPath.toString())
        }
    }
}