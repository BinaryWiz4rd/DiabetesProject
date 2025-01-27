package com.example.diabetesapp.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diabetesapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            // Handle the case where the user is not logged in
            return
        }

        // Launch a coroutine to fetch measurements
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val querySnapshot: QuerySnapshot = withContext(Dispatchers.IO) {
                    db.collection("glucose_measurements")
                        .whereEqualTo("userId", userId)
                        .get()
                        .await() // Await the task to get the actual QuerySnapshot
                }

                measurements.clear()
                for (document in querySnapshot.documents) {
                    val measurement = document.toObject(GlucoseMeasurement::class.java)
                    if (measurement != null) {
                        measurement.id = document.id // Set the document ID
                        measurements.add(measurement)
                    }
                }
                adapter.notifyDataSetChanged() // Notify adapter of data change
            } catch (exception: Exception) {
                // Handle the error (e.g., show a toast or log the error)
                exception.printStackTrace()
            }
        }
    }
}
