package com.example.parkingmanagementsystem.utils.extentions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar

fun View.hideSoftKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}
fun View.snackBar(msg: String) = Snackbar.make(this, msg, Snackbar.LENGTH_SHORT).show()
