package com.example.diabetesapp.code

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.diabetesapp.R
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class ChartFragment : Fragment() {

    private lateinit var lineGraphView: GraphView
    private lateinit var glucoseMeasurements: LineGraphSeries<DataPoint>
    private var nextXValue = 0.0 // Track the next X value for the graph

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chart, container, false)

        lineGraphView = view.findViewById(R.id.glucose_chart)

        setupGraph()

        return view
    }

    private fun setupGraph() {
        glucoseMeasurements = LineGraphSeries()
        lineGraphView.viewport.setMinX(0.0)
        lineGraphView.viewport.setMaxX(24.0)
        lineGraphView.viewport.setMinY(0.0)
        lineGraphView.viewport.isScrollable = true
        lineGraphView.viewport.isScalable = true
        lineGraphView.viewport.setScalableY(true)
        lineGraphView.viewport.setScrollableY(true)
        glucoseMeasurements.color = resources.getColor(R.color.blue_dark, null)

        lineGraphView.addSeries(glucoseMeasurements)
    }

    fun showAddDataPointDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_data_point, null)

        val inputGlucose = dialogView.findViewById<EditText>(R.id.input_glucose)
        val inputTime = dialogView.findViewById<EditText>(R.id.input_time)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Data Point")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val glucoseValue = inputGlucose.text.toString().toDoubleOrNull()
                val timeValue = inputTime.text.toString().toDoubleOrNull()

                if (glucoseValue != null && timeValue != null) {
                    addDataPoint(timeValue, glucoseValue)
                } else {
                    Toast.makeText(requireContext(), "Invalid input values", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }


    private fun addDataPoint(x: Double, y: Double) {
        glucoseMeasurements.appendData(DataPoint(x, y), true, 50)
        nextXValue = maxOf(nextXValue, x + 1) // Update nextXValue to avoid overlap
    }
}
