package com.example.mbircu.digitsrecognizer

import android.view.View

fun View.setOnClickListener(callback: () -> Unit) = this.setOnClickListener { callback() }