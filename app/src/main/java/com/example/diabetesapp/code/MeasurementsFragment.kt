package com.example.diabetesapp.code

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.diabetesapp.R
import com.example.diabetesapp.databinding.FragmentMeasurementsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class MeasurementsFragment : Fragment() {

    private lateinit var binding: FragmentMeasurementsBinding
    private lateinit var graphView: GraphView
    private val firestore = FirebaseFirestore.getInstance()
    private var series: LineGraphSeries<DataPoint> = LineGraphSeries()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMeasurementsBinding.inflate(inflater, container, false)
        graphView = binding.graphView

        setupGraphView()
        listenToGlucoseMeasurements()

        return binding.root
    }

    private fun setupGraphView() {
        series.color = ContextCompat.getColor(requireContext(), R.color.blue_dark)
        series.thickness = 6
        series.isDrawDataPoints = true
        series.dataPointsRadius = 8f

        graphView.addSeries(series)

        val gridLabelRenderer = graphView.gridLabelRenderer
        gridLabelRenderer.gridColor = Color.LTGRAY
        gridLabelRenderer.isHorizontalLabelsVisible = true
        gridLabelRenderer.isVerticalLabelsVisible = true
        gridLabelRenderer.numHorizontalLabels = 5
        gridLabelRenderer.numVerticalLabels = 7
        gridLabelRenderer.textSize = 36f
        gridLabelRenderer.verticalAxisTitle = "Glucose (mg/dL)"
        gridLabelRenderer.horizontalAxisTitle = "Time "

        graphView.viewport.isYAxisBoundsManual = true
        graphView.viewport.setMinY(0.0)
        graphView.viewport.setMaxY(350.0)

        graphView.viewport.isXAxisBoundsManual = true
        graphView.viewport.setMinX(0.0)
        graphView.viewport.setMaxX(24.0)

        graphView.viewport.isScalable = true
        graphView.viewport.isScrollable = true
    }

    private fun listenToGlucoseMeasurements() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        firestore.collection("users").document(userId)
            .collection("glucose_measurements")
            .orderBy("time")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    updateGraph(snapshots)
                } else {
                    Toast.makeText(context, "No data available.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateGraph(snapshots: QuerySnapshot) {
        val dataPoints = mutableListOf<DataPoint>()
        var latestValue: Double? = null
        var index = 0.0

        for (document in snapshots) {
            val value = document.getLong("value")?.toDouble() ?: continue
            dataPoints.add(DataPoint(index, value))
            latestValue = value
            index++
        }

        if (dataPoints.isNotEmpty()) {
            series.resetData(dataPoints.toTypedArray())
            latestValue?.let { displayLatestGlucoseValue(it) }
        } else {
            Toast.makeText(context, "No valid measurements found.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayLatestGlucoseValue(value: Double) {
        binding.glucoseValue.text = String.format("%.0f mg/dL", value)
        binding.glucoseValue.setTextColor(
            if (value in 70.0..180.0) ContextCompat.getColor(requireContext(), R.color.green)
            else ContextCompat.getColor(requireContext(), R.color.red)
        )
    }
}
