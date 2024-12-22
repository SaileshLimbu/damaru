package com.powersoft.damaru.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.powersoft.damaru.R
import com.powersoft.damaru.base.BaseActivity
import com.powersoft.damaru.base.BaseViewModel
import com.powersoft.damaru.databinding.ActivityPinBinding
import com.powersoft.damaru.models.ErrorResponse
import com.powersoft.damaru.models.UserEntity
import com.powersoft.damaru.ui.LoginActivity
import com.powersoft.damaru.ui.helper.AlertHelper
import com.powersoft.damaru.ui.helper.ResponseCallback
import com.powersoft.damaru.viewmodels.PinViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainCoroutineDispatcher

@AndroidEntryPoint
class PinActivity : BaseActivity() {
    private val viewModel: PinViewModel by viewModels()
    private lateinit var binding: ActivityPinBinding
    private val enteredPin = StringBuilder()
    var resetPin = false
    var pinOnce = ""

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupKeypad()

        resetPin = intent.getBooleanExtra("resetPin", false)
        if (resetPin) {
            binding.pinPrompt.text = getString(R.string.enter_your_new_pin)
        }
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
        if (resetPin) {
            if (pinOnce.isEmpty()) {
                pinOnce = enteredPin.toString()
                enteredPin.clear()
                binding.pinPrompt.text = getString(R.string.re_enter_your_pin)
                updatePinDots()
            } else {
                if (pinOnce == enteredPin.toString()) {
                    viewModel.resetPin(enteredPin.toString(), object : ResponseCallback {
                        override fun onResponse(any: Any, errorResponse: ErrorResponse?) {
                            if (errorResponse == null) {
                                if (resetPin) {
                                    startActivity(Intent(this@PinActivity, MainActivity::class.java))
                                }
                                return
                            } else {
                                AlertHelper.showAlertDialog(
                                    this@PinActivity, errorResponse.message?.error ?: getString(R.string.error),
                                    errorResponse.message?.message ?: getString(R.string.error)
                                )
                            }
                        }
                    })
                    pinOnce = ""
                    enteredPin.clear()
                    updatePinDots()
                } else {
                    pinOnce = ""
                    enteredPin.clear()
                    binding.pinPrompt.text = getString(R.string.enter_your_new_pin)
                    updatePinDots()
                    AlertHelper.showSnackbar(binding.root, getString(R.string.pin_does_not_matches))
                }
            }
            return
        }

        //do something
    }
}
