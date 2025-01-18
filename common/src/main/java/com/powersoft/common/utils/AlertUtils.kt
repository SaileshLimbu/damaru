package com.powersoft.common.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.powersoft.common.R
import com.powersoft.common.databinding.AlertEditBinding
import com.powersoft.common.databinding.DialogMessageBinding
import com.powersoft.common.databinding.DialogOptionBinding
import com.powersoft.common.databinding.ItemOptionBinding
import com.powersoft.common.model.OptionItem
import com.powersoft.common.ui.helper.ResponseCallback

object AlertUtils {
    fun showMessage(context: Context?, title: String?, message: String?, onOkClick: (() -> Unit)? = null) {
        val dialog = AlertDialog.Builder(context!!).create()
        val b = DialogMessageBinding.inflate(LayoutInflater.from(context), null, false)
        dialog.setView(b.getRoot())
        b.imgClose.setOnClickListener { dialog.dismiss() }

        b.tvTitle.text = title
        b.tvMessage.text = message

        b.btnOk.setOnClickListener {
            onOkClick?.invoke()
            dialog.dismiss()
        }

        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.show()
    }

    fun showConfirmDialog(
        context: Context?,
        title: String,
        message: String,
        positiveButtonText: String? = null,
        negativeButtonText: String? = null,
        onOkClick: () -> Unit
    ) {
        val dialog = AlertDialog.Builder(context!!).create()
        val b = DialogMessageBinding.inflate(LayoutInflater.from(context), null, false)
        dialog.setView(b.getRoot())
        b.btnCancel.show()
        b.imgClose.setOnClickListener { dialog.dismiss() }
        b.btnCancel.setOnClickListener { dialog.dismiss() }

        negativeButtonText?.let { b.btnCancel.text = it }
        positiveButtonText?.let { b.btnOk.text = it }

        b.tvTitle.text = title
        b.tvMessage.text = message

        b.btnOk.setOnClickListener {
            onOkClick()
            dialog.dismiss()
        }

        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.show()
    }


    fun showOptionDialog(
        context: Context?,
        title: String,
        options: Array<OptionItem>,
        onOptionSelected: (option: Int) -> Unit
    ) {
        val dialog = AlertDialog.Builder(context!!).create()
        val b = DialogOptionBinding.inflate(LayoutInflater.from(context), null, false)
        dialog.setView(b.getRoot())
        b.imgClose.setOnClickListener { dialog.dismiss() }

        b.tvTitle.text = title

        options.forEachIndexed { index, option ->
            val itemBinding = ItemOptionBinding.inflate(LayoutInflater.from(context), b.root, false)
            itemBinding.tvOption.text = option.text

            val drawable = ContextCompat.getDrawable(context, option.icon)
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            itemBinding.tvOption.setCompoundDrawables(drawable, null, null, null)

            itemBinding.root.setOnClickListener {
                onOptionSelected(index)
                dialog.dismiss()
            }
            b.viewOptions.addView(itemBinding.root)
        }

        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.show()
    }

    fun showEditAlert(
        context: Context,
        deviceName: String,
        details: String,
        onSaveClicked: (updatedDetails: String) -> Unit
    ) {
        val alertDialog = AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
        val alertBinding = AlertEditBinding.inflate(LayoutInflater.from(context))
        alertDialog.setView(alertBinding.root)
        val dialog = alertDialog.create()
        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        alertBinding.btnClose2.setOnClickListener {
            dialog.dismiss()
        }

        alertBinding.btnClose.setOnClickListener {
            dialog.dismiss()
        }

        alertBinding.tvDeviceName.text = deviceName
        alertBinding.tvDeviceId.hide()
        alertBinding.tvAssignedTo.hide()
        alertBinding.txtInputLayout.editText?.setText(details)

        alertBinding.btnExtend.setOnClickListener {
            onSaveClicked(alertBinding.etDetails.text.toString())
            dialog.dismiss()
        }


        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation

        dialog.show()
    }

    fun showToast(context: Context, @StringRes messageRes: Int, duration: Int = Toast.LENGTH_SHORT) {
        val toast = Toast.makeText(context, messageRes, duration)
        toast.show()
    }
}