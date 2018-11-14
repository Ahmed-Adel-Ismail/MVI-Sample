package com.mvi.presentation

import android.widget.TextView

fun TextView.asString() = text?.toString() ?: ""