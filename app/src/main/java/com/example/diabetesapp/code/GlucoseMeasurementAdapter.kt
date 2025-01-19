package com.example.diabetesapp.code

import GlucoseMeasurement
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.diabetesapp.R
import com.google.firebase.firestore.FirebaseFirestore

class GlucoseMeasurementAdapter(
    private val measurements: List<GlucoseMeasurement>,
    private val onItemClick: (GlucoseMeasurement) -> Unit
) : RecyclerView.Adapter<GlucoseMeasurementAdapter.MeasurementViewHolder>() {

    class MeasurementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val glucoseValue: TextView = view.findViewById(R.id.glucose_value)
        val timeValue: TextView = view.findViewById(R.id.time_value)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_glucose_measurement, parent, false)
        return MeasurementViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeasurementViewHolder, position: Int) {
        val measurement = measurements[position]
        holder.glucoseValue.text = measurement.value.toString()
        holder.timeValue.text = measurement.time.toString()

        holder.itemView.setOnClickListener {
            onItemClick(measurement)
        }

        holder.itemView.setOnLongClickListener {
            deleteMeasurement(measurement, holder.itemView.context)
            true
        }
    }

    private fun deleteMeasurement(measurement: GlucoseMeasurement, context: Context) {
        val db = FirebaseFirestore.getInstance()
        db.collection("glucose_measurements").document(measurement.id).delete()
            .addOnSuccessListener {
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount() = measurements.size
}