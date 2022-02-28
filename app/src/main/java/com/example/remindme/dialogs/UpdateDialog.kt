package com.example.remindme.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.remindme.R

class UpdateDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val spanStr = SpannableString(this.getText(R.string.link))
        val linkTV = TextView(this.context)
        linkTV.text = spanStr

        //link to site to download updated version
        linkTV.setOnClickListener {
            val uri = Uri.parse("https://appsbyanl.herokuapp.com/")
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        //style textview
        linkTV.gravity = Gravity.CENTER
        linkTV.setPadding(0,15,0,0)
        linkTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)

        return AlertDialog.Builder(activity)
            .setTitle(R.string.update)
            .setCancelable(true)
            .setPositiveButton(R.string.dismiss_btn, null)
            .setView(linkTV)
            .create()
    }//onCreateDialog

}