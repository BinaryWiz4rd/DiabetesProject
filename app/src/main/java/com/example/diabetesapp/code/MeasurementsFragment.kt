package com.example.diabetesapp.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diabetesapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MeasurementsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GlucoseMeasurementAdapter
    private var measurements: MutableList<GlucoseMeasurement> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_measurements, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = GlucoseMeasurementAdapter(measurements) { measurement ->
            // Handle item click (e.g., show details or edit)
        }
        recyclerView.adapter = adapter
        fetchMeasurements()
        return view
    }

    private fun fetchMeasurements() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser ?.uid
        db.collection("glucose_measurements")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                measurements.clear()
                for (document in documents) {
                    val measurement = document.toObject(GlucoseMeasurement::class.java)
                    measurement.id = document.id // Set the document ID
                    measurements.add(measurement)
                }
                adapter.notifyDataSetChanged() // Notify adapter of data change
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }
}