package com.example.diabetesapp.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.diabetesapp.R

/**
 * Fragment for calculating net carbohydrates.
 *
 * Website: <https://cukrowy.pl/>
 */
class CalculatorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calculator, container, false)

        val carbInput = view.findViewById<EditText>(R.id.carb_input)
        val fiberInput = view.findViewById<EditText>(R.id.fiber_input)
        val weightInput = view.findViewById<EditText>(R.id.weight_input)
        val calculateButton = view.findViewById<Button>(R.id.calculate_button)
        val resultView = view.findViewById<TextView>(R.id.result_view)

        calculateButton.setOnClickListener {
            val carbs = carbInput.text.toString().toDoubleOrNull() ?: 0.0
            val fiber = fiberInput.text.toString().toDoubleOrNull() ?: 0.0
            val weight = weightInput.text.toString().toDoubleOrNull() ?: 1.0

            val netCarbs = (carbs - fiber) / 10 * (weight / 100)
            resultView.text = String.format("Net Carbohydrates: %.2f", netCarbs)
        }

        return view
    }
}