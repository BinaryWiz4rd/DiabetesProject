package com.example.diabetesapp.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diabetesapp.code.GlucoseMeasurement
import com.example.diabetesapp.code.GlucoseMeasurementAdapter
import com.example.diabetesapp.databinding.FragmentMeasurementsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MeasurementsFragment : Fragment() {

    private lateinit var binding: FragmentMeasurementsBinding
    private lateinit var adapter: GlucoseMeasurementAdapter
    private val measurementsList = mutableListOf<GlucoseMeasurement>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMeasurementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = GlucoseMeasurementAdapter(measurementsList)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    fun addGlucoseMeasurement(value: Int, time: Long) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val measurement = GlucoseMeasurement(value, time, userId)

        db.collection("users").document(userId)
            .collection("glucose_measurements")
            .add(measurement)
            .addOnSuccessListener {
                fetchMeasurements()
                Toast.makeText(requireContext(), "Measurement added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to add: ${e.message}", Toast.LENGTH_SHORT).show()
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
                    measurementsList.add(GlucoseMeasurement(value, time))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to load data: $exception", Toast.LENGTH_SHORT).show()
            }
    }
}
