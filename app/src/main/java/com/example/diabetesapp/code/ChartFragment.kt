package com.example.diabetesapp.code

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.diabetesapp.R

// wykresik z cukrami
// https://www.youtube.com/watch?v=9gfp_nT8p7g
// https://github.com/PhilJay/MPAndroidChart
//https://www.youtube.com/watch?v=ufaK_Hd6BpI
class ChartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

}