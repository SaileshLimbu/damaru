package com.d1vivek.projectz.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.d1vivek.projectz.R
import com.d1vivek.projectz.databinding.ActivityPinBinding

class PinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPinBinding
    private val enteredPin = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupKeypad()
    }

    private fun setupKeypad() {
        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2,
            binding.btn3, binding.btn4, binding.btn5,
            binding.btn6, binding.btn7, binding.btn8,
            binding.btn9
        )

        numberButtons.forEachIndexed { index, button ->
            button.setOnClickListener { addDigit(index) }
        }

        binding.btnClear.setOnClickListener {
            removeDigit()
        }
    }

    private fun addDigit(digit: Int) {
        if (enteredPin.length < 4) {
            enteredPin.append(digit)
            updatePinDots()
        }

        if (enteredPin.length == 4) {
            verifyPin()
        }
    }

    private fun removeDigit() {
        if (enteredPin.isNotEmpty()) {
            enteredPin.deleteCharAt(enteredPin.length - 1)
            updatePinDots()
        }
    }

    private fun updatePinDots() {
        val pinDots = listOf(
            binding.input1, binding.input2, binding.input3, binding.input4
        )

        pinDots.forEachIndexed { index, dot ->
            if (index < enteredPin.length) {
                dot.setBackgroundResource(R.drawable.circle_masked)
            } else {
                dot.setBackgroundResource(R.drawable.circle_empty)
            }
        }
    }

    private fun verifyPin() {
        val correctPin = "1234"
        if (enteredPin.toString() == correctPin) {
            startActivity(Intent(this@PinActivity, DeviceControlActivity::class.java))
        } else {
            enteredPin.clear()
            updatePinDots()
        }
    }
}
