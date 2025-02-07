package com.example.diabetesapp.code

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Fragment for displaying the history of glucose measurements.
 *
 * This fragment fetches and displays a list of glucose measurements from Firestore,
 * allowing users to edit or delete measurements.
 */
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
            ::onEditMeasurement, //referention to function
            ::onDeleteMeasurement
        )
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = glucoseMeasurementAdapter
        fetchMeasurements()
        val exportButton: Button = view.findViewById(R.id.export_button)
        exportButton.setOnClickListener {
            exportMeasurementsToCSV()
        }
        return view
    }

    private fun exportMeasurementsToCSV() {
        val csvFileName = "glucose_measurements.csv"

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Toast.makeText(requireContext(), "External storage not available", Toast.LENGTH_SHORT).show()
            return
        }

        val dir = requireContext().getExternalFilesDir(null)
        val file = File(dir, csvFileName)

        try {
            val fileWriter = FileWriter(file)
            fileWriter.append("Value,Time,User  Id,Id\n")

            val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

            for (measurement in measurementsList) {
                val formattedTime = dateFormat.format(measurement.time)
                fileWriter.append("${measurement.value},$formattedTime,${measurement.userId},${measurement.id}\n")
            }

            fileWriter.flush()
            fileWriter.close()

            Toast.makeText(requireContext(), "Data exported to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Error exporting data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens a dialog to edit a glucose measurement.
     *
     * @param measurement The measurement to be edited.
     */
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

    /**
     * Opens a confirmation dialog to delete a glucose measurement.
     *
     * @param measurement The measurement to be deleted.
     */
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

    /**
     * Updates a glucose measurement in Firestore.
     *
     * @param measurement The measurement to be updated.
     * @param newGlucoseValue The new glucose value to set.
     */
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

    /**
     * Deletes a glucose measurement from Firestore.
     *
     * @param measurement The measurement to be deleted.
     */
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

    /**
     * Fetches glucose measurements from Firestore and updates the RecyclerView.
     */
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

    /**
     * Adds a new glucose measurement to Firestore.
     *
     * @param value The glucose value to add.
     * @param time The timestamp of the measurement.
     */
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