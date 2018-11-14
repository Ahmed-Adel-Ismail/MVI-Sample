package com.mvi.presentation

import android.content.Context
import android.widget.TextView
import android.widget.Toast

fun TextView.asString() = text?.toString() ?: ""

fun Context.longToast(message: String) {
    Toast.makeText(this,message,Toast.LENGTH_LONG).show()
}

fun Context.shortToast(message: String) {
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}