package com.example.diabetesapp.code

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diabetesapp.R

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
    }

    override fun getItemCount() = measurements.size
}