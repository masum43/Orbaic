package com.orbaic.miner.common

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.orbaic.miner.R

class ErrorDialog(private val activity: Activity) {
    private var dialog: Dialog? = null

    fun showTimeDiffWithServerError(errorMessage: String) {
        if (dialog != null && dialog!!.isShowing) {
            return
        }
        
        dialog = Dialog(activity)
        dialog!!.setContentView(R.layout.dialog_extra_point)
        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val holderBg = dialog!!.findViewById<LinearLayout>(R.id.holderBg)
        val tvNotice = dialog!!.findViewById<TextView>(R.id.tvNotice)
        tvNotice.text = errorMessage
        holderBg.setBackgroundColor(ContextCompat.getColor(activity, R.color.red))
        dialog!!.findViewById<View>(R.id.okButton).setOnClickListener { view: View? ->
            activity.finishAffinity()
        }
        dialog!!.show()
    }

    fun dismissDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }
}
