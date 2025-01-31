package com.example.diabetesapp.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.diabetesapp.R
import com.example.diabetesapp.databinding.FragmentMeasurementsBinding
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.helper.StaticLabelsFormatter
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MeasurementsFragment : Fragment() {

    private lateinit var binding: FragmentMeasurementsBinding
    private lateinit var graphView: GraphView
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMeasurementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        graphView = binding.graphView

        graphView.viewport.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_dark))
        graphView.viewport.isScalable = true
        graphView.viewport.isScrollable = true
        graphView.viewport.setScalableY(true)
        graphView.viewport.setScrollableY(true)

        graphView.gridLabelRenderer.isHorizontalLabelsVisible = true
        graphView.gridLabelRenderer.isVerticalLabelsVisible = true
        graphView.gridLabelRenderer.setVerticalLabelsColor(ContextCompat.getColor(requireContext(), R.color.white))
        graphView.gridLabelRenderer.setHorizontalLabelsColor(ContextCompat.getColor(requireContext(), R.color.white))

        firestore.collection("glucose_measurements")
            .get()
            .addOnSuccessListener { documents ->
                val glucoseData = mutableListOf<DataPoint>()

                for (document in documents) {
                    val timestamp = document.getTimestamp("timestamp")?.toDate()
                    val glucoseLevel = document.getDouble("glucose_level")

                    if (timestamp != null && glucoseLevel != null) {
                        val timeInMillis = timestamp.time
                        glucoseData.add(DataPoint(timeInMillis.toDouble(), glucoseLevel))
                    }
                }

                if (glucoseData.isNotEmpty()) {
                    val series = LineGraphSeries(glucoseData.toTypedArray())
                    series.color = ContextCompat.getColor(requireContext(), R.color.blue_dark) // Line color
                    series.thickness = 5
                    graphView.addSeries(series)

                    val dateFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val labelsFormatter = StaticLabelsFormatter(graphView)

                    val labels = glucoseData.map {
                        val date = Date(it.x.toLong())
                        dateFormatter.format(date)
                    }

                    labelsFormatter.setHorizontalLabels(labels.toTypedArray())
                    graphView.gridLabelRenderer.labelFormatter = labelsFormatter

                    graphView.viewport.setMinX(glucoseData.first().x)
                    graphView.viewport.setMaxX(glucoseData.last().x)
                    graphView.viewport.setMinY(glucoseData.minByOrNull { it.y }?.y ?: 0.0)
                    graphView.viewport.setMaxY(glucoseData.maxByOrNull { it.y }?.y ?: 10.0)  // Adjust based on data
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
    }
}
