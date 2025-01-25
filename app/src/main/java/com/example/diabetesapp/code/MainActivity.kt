package com.example.diabetesapp.code

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import android.os.Bundle
import android.util.Log
import com.example.diabetesapp.R
import com.example.diabetesapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(MeasurementsFragment())
        binding.bottomNavigationView.background = null

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.measurements -> replaceFragment(MeasurementsFragment())
                R.id.history -> replaceFragment(HistoryFragment())
                R.id.calculator -> replaceFragment(CalculatorFragment())
                R.id.library -> replaceFragment(LibraryFragment())
                //R.id.fab ->
            }
            true
        }

        binding.bottomNavigationView.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
            //if (currentFragment is MeasurementsFragment) {
             //   currentFragment.showAddDataPointDialog()
            //}
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}