package com.ditta.apptracker.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog

object ViewUtils {

    fun createDialog(
        context: Context,
        @StringRes
        title: Int,
        @StringRes
        message: Int,
        cancellable: Boolean,
        @StringRes
        labelPositiveBtn: Int,
        onPositiveBtnClick: () -> Unit
    ): AlertDialog {

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(cancellable)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(
            labelPositiveBtn
        ) { _, _ ->
            onPositiveBtnClick()
        }

        return builder.create()
    }

}