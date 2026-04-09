package com.example.unitconverter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import kotlin.math.round

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputValue = findViewById<EditText>(R.id.inputValue)
        val fromUnit = findViewById<Spinner>(R.id.fromUnit)
        val toUnit = findViewById<Spinner>(R.id.toUnit)
        val convertBtn = findViewById<Button>(R.id.convertBtn)
        val result = findViewById<TextView>(R.id.result)

        // Spinner adapter
        val units = resources.getStringArray(R.array.units)
        fromUnit.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, units)
        toUnit.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, units)

        convertBtn.setOnClickListener {
            val value = inputValue.text.toString()

            if (value.isEmpty()) {
                Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val num = value.toDouble()
            val unit1 = fromUnit.selectedItem.toString()
            val unit2 = toUnit.selectedItem.toString()

            val converted = convertUnits(num, unit1, unit2)
            result.text = "Result: $converted"
        }
    }

    // Conversion logic
    fun convertUnits(value: Double, from: String, to: String): Double {
        return when (from + "-" + to) {
            "Centimeter-Meter" -> value / 100
            "Meter-Centimeter" -> value * 100

            "Gram-Kilogram" -> value / 1000
            "Kilogram-Gram" -> value * 1000

            else -> value  // same units
        }
    }
}