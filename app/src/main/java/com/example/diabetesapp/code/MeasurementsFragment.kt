package com.example.diabetesapp.code

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.diabetesapp.databinding.FragmentMeasurementsBinding
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

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
        graphView.addSeries(series)
        graphView.viewport.isXAxisBoundsManual = true
        graphView.viewport.isYAxisBoundsManual = true
        graphView.viewport.setMinX(0.0)
        graphView.viewport.setMaxX(10.0)
        graphView.viewport.setMinY(50.0)
        graphView.viewport.setMaxY(300.0)
        graphView.viewport.isScalable = true
        graphView.viewport.isScrollable = true
    }

    private fun listenToGlucoseMeasurements() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(context, "User not authenticated.", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users").document(userId)
            .collection("glucose_measurements")
            .orderBy("time")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("FirestoreError", "Error fetching data: ${e.message}", e)
                    Toast.makeText(context, "Error fetching data: ${e.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    Log.d("FirestoreData", "Documents: ${snapshots.documents}")
                    updateGraph(snapshots)
                } else {
                    Toast.makeText(context, "No data available.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateGraph(snapshots: QuerySnapshot) {
        val dataPoints = mutableListOf<DataPoint>()
        var index = 0.0

        for (document in snapshots) {
            val value = document.getLong("value")?.toDouble()
            if (value != null) {
                dataPoints.add(DataPoint(index, value))
                index++
            } else {
                Log.w("InvalidData", "Document ${document.id} has no valid 'value' field")
            }
        }

        if (dataPoints.isNotEmpty()) {
            series.resetData(dataPoints.toTypedArray())
        } else {
            series.resetData(emptyArray())
            Toast.makeText(context, "No valid measurements found.", Toast.LENGTH_SHORT).show()
        }
    }
}
