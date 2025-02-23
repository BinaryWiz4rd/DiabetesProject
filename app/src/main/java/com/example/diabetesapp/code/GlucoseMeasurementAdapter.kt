package com.example.diabetesapp.code

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diabetesapp.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter for displaying a list of glucose measurements in a RecyclerView.
 *
 * @property measurementsList The list of glucose measurements to display.
 * @property onEditMeasurement Callback function to handle editing a measurement.
 * @property onDeleteMeasurement Callback function to handle deleting a measurement.
 */
class GlucoseMeasurementAdapter(
    private val measurementsList: List<GlucoseMeasurement>,
    private val onEditMeasurement: (GlucoseMeasurement) -> Unit,
    private val onDeleteMeasurement: (GlucoseMeasurement) -> Unit
) : RecyclerView.Adapter<GlucoseMeasurementAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_glucose_measurement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val measurement = measurementsList[position]
        holder.valueTextView.text = "Glucose: ${measurement.value} mg/dL"

        val date = Date(measurement.time)
        val timeFormatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
        holder.timeTextView.text = "Time: $timeFormatted"

        holder.editButton.setOnClickListener {
            onEditMeasurement(measurement)
        }

        holder.deleteButton.setOnClickListener {
            onDeleteMeasurement(measurement)
        }
    }

    override fun getItemCount(): Int = measurementsList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val valueTextView: TextView = itemView.findViewById(R.id.textViewValue)
        val timeTextView: TextView = itemView.findViewById(R.id.textViewTime)
        val editButton: Button = itemView.findViewById(R.id.btnEdit)
        val deleteButton: Button = itemView.findViewById(R.id.btnDelete)
    }
}