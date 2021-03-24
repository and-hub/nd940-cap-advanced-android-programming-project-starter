package com.example.android.politicalpreparedness.utils

import java.text.SimpleDateFormat
import java.util.*

fun Date.toFormattedString(): String {
    val dateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
    return dateFormat.format(this)
}