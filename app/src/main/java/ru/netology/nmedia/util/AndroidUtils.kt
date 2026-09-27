package ru.netology.nmedia.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

object AndroidUtils {

    fun hideKeyboard(view: View) {
        val manager =
            view.context.getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager

        manager.hideSoftInputFromWindow(
            view.windowToken,
            0
        )
    }

    fun showKeyboard(view: View) {
        view.requestFocus()

        view.post {
            val manager =
                view.context.getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager

            manager.showSoftInput(
                view,
                InputMethodManager.SHOW_IMPLICIT
            )
        }
    }
}