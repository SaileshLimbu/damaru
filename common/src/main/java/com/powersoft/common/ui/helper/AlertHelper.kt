package com.powersoft.common.ui.helper

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.provider.CalendarContract.Colors
import android.view.Gravity
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT

object AlertHelper {
    fun showAlertDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String = "OK",
        negativeButtonText: String? = null,
        onPositiveButtonClick: (() -> Unit)? = null,
        onNegativeButtonClick: (() -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                onPositiveButtonClick?.invoke()
                dialog.dismiss()
            }

        if (negativeButtonText != null) {
            builder.setNegativeButton(negativeButtonText) { dialog, _ ->
                onNegativeButtonClick?.invoke()
                dialog.dismiss()
            }
        }

        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
    }

    fun showSnackbar(view: View, message: String, length: Int = LENGTH_SHORT) {
        val snackbar = Snackbar.make(view, message, length)
        snackbar.setBackgroundTint(view.context.getColor(android.R.color.holo_blue_light))
        snackbar.setTextColor(view.context.getColor(android.R.color.white))
        snackbar.setAction("Dismiss") {
            snackbar.dismiss()
        }
        snackbar.show()
    }

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        val toast = Toast.makeText(context, message, duration)
        toast.show()
    }

    fun showToast(context: Context, @StringRes messageRes: Int, duration: Int = Toast.LENGTH_SHORT) {
        val toast = Toast.makeText(context, messageRes, duration)
        toast.show()
    }

    fun showError(context: Context, errorMessage: String) {
        showSnackbar(context.getRootView(), errorMessage)
        showToast(context, errorMessage)
        showAlertDialog(
            context,
            "Error",
            errorMessage,
            positiveButtonText = "OK"
        )
    }

    private fun Context.getRootView(): View {
        return (this as? androidx.appcompat.app.AppCompatActivity)?.window?.decorView?.findViewById(android.R.id.content) ?: throw IllegalStateException("Root view not found.")
    }
}
