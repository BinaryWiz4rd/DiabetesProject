package com.example.diabetesapp.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diabetesapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var glucoseMeasurementAdapter: GlucoseMeasurementAdapter
    private val measurementsList = mutableListOf<GlucoseMeasurement>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewHistory)

        glucoseMeasurementAdapter = GlucoseMeasurementAdapter(
            measurementsList,
            ::onEditMeasurement,
            ::onDeleteMeasurement
        )

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = glucoseMeasurementAdapter

        fetchMeasurements()

        return view
    }

    private fun onEditMeasurement(measurement: GlucoseMeasurement) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_update_measurement, null)
        val glucoseInput = dialogView.findViewById<EditText>(R.id.editTextGlucose)
        val timeInput = dialogView.findViewById<EditText>(R.id.editTextTime)

        glucoseInput.setText(measurement.value.toString())
        timeInput.setText(SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(measurement.time)))

        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Edit Glucose Measurement")
            .setView(dialogView)
            .setPositiveButton("Update") { dialog, _ ->
                val newGlucoseValue = glucoseInput.text.toString().toIntOrNull()
                val newTime = timeInput.tag as? Long

                if (newGlucoseValue != null && newTime != null) {
                    updateMeasurement(measurement, newGlucoseValue, newTime)
                } else {
                    Toast.makeText(requireContext(), "Invalid input. Please provide valid values.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        dialogBuilder.create().show()
    }

    private fun onDeleteMeasurement(measurement: GlucoseMeasurement) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Measurement")
            .setMessage("Are you sure you want to delete this measurement?")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteMeasurement(measurement)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun updateMeasurement(measurement: GlucoseMeasurement, newGlucoseValue: Int, newTime: Long) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("glucose_measurements")
            .document(measurement.id)
            .update("value", newGlucoseValue, "time", newTime)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Measurement updated", Toast.LENGTH_SHORT).show()
                fetchMeasurements()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to update: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteMeasurement(measurement: GlucoseMeasurement) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("glucose_measurements")
            .document(measurement.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Measurement deleted", Toast.LENGTH_SHORT).show()
                fetchMeasurements()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to delete: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchMeasurements() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("glucose_measurements")
            .get()
            .addOnSuccessListener { result ->
                measurementsList.clear()
                for (document in result) {
                    val value = document.getLong("value")?.toInt() ?: 0
                    val time = document.getLong("time") ?: 0
                    val id = document.id
                    measurementsList.add(GlucoseMeasurement(value, time, userId, id))
                }
                glucoseMeasurementAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to load data: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    fun addGlucoseMeasurement(value: Int, time: Long) {
        Toast.makeText(requireContext(), "Glucose value $value added at ${Date(time)}", Toast.LENGTH_SHORT).show()

    }
}
