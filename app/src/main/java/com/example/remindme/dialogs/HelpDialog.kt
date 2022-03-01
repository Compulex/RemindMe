package com.example.remindme.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.remindme.R

class HelpDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val howTV = TextView(this.context)
        howTV.text = this.getString(R.string.howto)
        howTV.gravity = Gravity.CENTER
        howTV.setPadding(15,15,15,15)
        howTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)

        return AlertDialog.Builder(activity)
            .setTitle(R.string.help)
            .setView(howTV)
            .create()
    }
}