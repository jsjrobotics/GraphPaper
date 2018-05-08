package nyc.jsjrobotics.graphpaper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ChangeGraphPaperParamsView(context: Context) {
    val root: View

    init {
        root = LayoutInflater.from(context).inflate(R.layout.edit_graph_paper_params, null, false)
    }
}