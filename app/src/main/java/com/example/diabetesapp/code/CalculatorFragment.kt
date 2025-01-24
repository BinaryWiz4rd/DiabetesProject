package com.example.diabetesapp.code

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.diabetesapp.R

class CalculatorFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calculator, container, false)

        val totalCarbsInput = view.findViewById<EditText>(R.id.totalCarbsInput)
        val fiberInput = view.findViewById<EditText>(R.id.fiberInput)
        val portionSizeInput = view.findViewById<EditText>(R.id.portionSizeInput)
        val resultTextView = view.findViewById<TextView>(R.id.resultTextView)
        val calculateButton = view.findViewById<Button>(R.id.calculateButton)

        calculateButton.setOnClickListener {
            val totalCarbs = totalCarbsInput.text.toString().toDoubleOrNull()
            val fiber = fiberInput.text.toString().toDoubleOrNull()
            val portionSize = portionSizeInput.text.toString().toDoubleOrNull()

            if (totalCarbs == null || fiber == null || portionSize == null) {
                Toast.makeText(requireContext(), "Please enter valid inputs", Toast.LENGTH_SHORT).show()
            } else {
                val effectiveCarbs = ((totalCarbs - fiber) / 10) * (portionSize/10)
                resultTextView.text = String.format("Effective Carbs: %.2f g", effectiveCarbs)
            }
        }

        return view
    }
}


//https://cukrowy.pl/
//    Od całkowitej ilości węglowodanów zawartych w 100 g produktu odejmuje się ilość błonnika
//    pokarmowego, a następnie dzieli przez 10. Pamiętać przy tym trzeba jednak o gramaturze
//    realnie spożywanej porcji. Jeśli zatem jest to przykładowo 200 gramów, całość trzeba
//    przemnożyć przez 2.
