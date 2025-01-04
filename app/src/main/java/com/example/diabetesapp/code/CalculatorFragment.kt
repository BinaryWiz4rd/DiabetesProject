package com.example.diabetesapp.code

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.diabetesapp.R

class CalculatorFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calculator, container, false)
    }

    //https://cukrowy.pl/
//    Od całkowitej ilości węglowodanów zawartych w 100 g produktu odejmuje się ilość błonnika
//    pokarmowego, a następnie dzieli przez 10. Pamiętać przy tym trzeba jednak o gramaturze
//    realnie spożywanej porcji. Jeśli zatem jest to przykładowo 200 gramów, całość trzeba
//    przemnożyć przez 2.
}