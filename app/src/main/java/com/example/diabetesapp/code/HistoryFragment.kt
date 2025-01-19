package com.example.diabetesapp.code

import GlucoseMeasurement
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diabetesapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GlucoseMeasurementAdapter
    private var measurements: List<GlucoseMeasurement> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        fetchDataFromFirebase()

        return view
    }

    private fun fetchDataFromFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser ?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        db.collection("glucose_measurements")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    measurements = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(GlucoseMeasurement::class.java)?.apply { id = doc.id }
                    }
                    adapter = GlucoseMeasurementAdapter(measurements) { measurement ->
                        showMeasurementDetails(measurement)
                    }
                    recyclerView.adapter = adapter
                }
            }
    }

    @SuppressLint("MissingInflatedId")
    private fun showMeasurementDetails(measurement: GlucoseMeasurement) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_measurement, null)
        val inputGlucose = dialogView.findViewById<EditText>(R.id.input_glucose)
        val inputTime = dialogView.findViewById<EditText>(R.id.input_time)

        inputGlucose.setText(measurement.value.toString())
        inputTime.setText(measurement.time.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Measurement")
            .setView(dialogView)
            .setPositiveButton("Update") { dialog, which ->
                val newValue = inputGlucose.text.toString().toDoubleOrNull()
                val newTime = inputTime.text.toString().toDoubleOrNull()

                if (newValue != null && newTime != null) {
                    updateMeasurement(measurement, newValue, newTime)
                } else {
                    Toast.makeText(requireContext(), "Invalid input", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun updateMeasurement(measurement: GlucoseMeasurement, newValue: Double, newTime: Double) {
        val db = FirebaseFirestore.getInstance()
        db.collection("glucose_measurements").document(measurement.id).update(
            "value", newValue,
            "time", newTime
        ).addOnSuccessListener {
            Toast.makeText(requireContext(), "Measurement updated", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to update measurement", Toast.LENGTH_SHORT).show()
        }
    }
}