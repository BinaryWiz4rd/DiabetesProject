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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
        glucoseInput.setText(measurement.value.toString())
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Edit Glucose Measurement")
            .setView(dialogView)
            .setPositiveButton("Update") { dialog, _ ->
                val newGlucoseValue = glucoseInput.text.toString().toIntOrNull()
                if (newGlucoseValue != null) {
                    updateMeasurement(measurement, newGlucoseValue)
                } else {
                    Toast.makeText(requireContext(), "Invalid glucose value.", Toast.LENGTH_SHORT).show()
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

    private fun updateMeasurement(measurement: GlucoseMeasurement, newGlucoseValue: Int) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser ?.uid ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("users").document(userId)
                    .collection("glucose_measurements")
                    .document(measurement.id)
                    .update("value", newGlucoseValue)
                    .await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Measurement updated", Toast.LENGTH_SHORT).show()
                    fetchMeasurements()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to update: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteMeasurement(measurement: GlucoseMeasurement) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser ?.uid ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("users").document(userId)
                    .collection("glucose_measurements")
                    .document(measurement.id)
                    .delete()
                    .await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Measurement deleted", Toast.LENGTH_SHORT).show()
                    fetchMeasurements()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to delete: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchMeasurements() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser ?.uid ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = db.collection("users").document(userId)
                    .collection("glucose_measurements")
                    .get()
                    .await()

                measurementsList.clear()
                for (document in result) {
                    val value = document.getLong("value")?.toInt() ?: 0
                    val time = document.getLong("time") ?: 0
                    val id = document.id
                    measurementsList.add(GlucoseMeasurement(value, time, userId, id))
                }
                withContext(Dispatchers.Main) {
                    glucoseMeasurementAdapter.notifyDataSetChanged()
                }
            } catch (exception: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to load data: $exception", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun addGlucoseMeasurement(value: Int, time: Long) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser ?.uid ?: return
        val newMeasurement = GlucoseMeasurement(value = value, time = time, userId = userId)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("users").document(userId)
                    .collection("glucose_measurements")
                    .add(newMeasurement)
                    .await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Measurement added successfully!", Toast.LENGTH_SHORT).show()
                    fetchMeasurements()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to add measurement: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}