package com.powersoft.common.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PickerEntity(val value: String, val dataJson: String) : Parcelable