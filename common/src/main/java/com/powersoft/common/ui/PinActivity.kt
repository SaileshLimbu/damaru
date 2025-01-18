package com.powersoft.common.ui

import android.os.Bundle
import androidx.activity.viewModels
import com.powersoft.common.R
import com.powersoft.common.base.BaseActivity
import com.powersoft.common.base.BaseViewModel
import com.powersoft.common.databinding.ActivityPinBinding
import com.powersoft.common.ui.helper.ResponseCallback
import com.powersoft.common.utils.AlertUtils
import com.powersoft.common.utils.hide
import com.powersoft.common.viewmodels.PinViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class PinActivity : BaseActivity() {
    private val viewModel: PinViewModel by viewModels()
    private lateinit var binding: ActivityPinBinding
    private val enteredPin = StringBuilder()
    private var pinOnce = ""

    abstract fun onPinVerified()
    abstract fun onPinResetResponse(any: Any, errorMessage: String?)
    abstract fun onLogout(any: Any, errorMessage: String?)
    abstract fun getAccountId(): String
    abstract fun isChangePin(): Boolean

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupKeypad()

        binding.btnLogout.setOnClickListener {
            viewModel.logout(object : ResponseCallback {
                override fun onResponse(any: Any, errorMessage: String?) {
                    onLogout(any, errorMessage)
                }
            })
        }

        if (isChangePin()) {
            binding.btnLogout.hide()
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
        if (enteredPin.length < 5) {
            enteredPin.append(digit)
            updatePinDots()
        }

        if (enteredPin.length == 5) {
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
            binding.input1, binding.input2, binding.input3, binding.input4, binding.input5
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
        if (pinOnce.isEmpty()) {
            pinOnce = enteredPin.toString()
            enteredPin.clear()
            binding.pinPrompt.text = getString(R.string.re_enter_your_pin)
            updatePinDots()
        } else {
            if (pinOnce == enteredPin.toString()) {
                viewModel.resetPin(getAccountId(), enteredPin.toString(), object : ResponseCallback {
                    override fun onResponse(any: Any, errorMessage: String?) {
                        onPinResetResponse(any, errorMessage)
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
                AlertUtils.showMessage(this, "Error", getString(R.string.pin_does_not_matches))
            }
        }
        return
    }
}
