package com.example.diabetesapp.code

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.diabetesapp.R

/**
 * Fragment for displaying library information.
 *
 * This fragment contains a button that provides additional information when clicked.
 */
class LibraryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val learnMoreButton: Button = view.findViewById(R.id.learn_more_button)
        learnMoreButton.setOnClickListener {
            Toast.makeText(requireContext(), "More information coming soon!", Toast.LENGTH_SHORT).show()
        }
    }
}