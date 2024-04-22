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

class CustomDialog(private val activity: Activity) {
    private var dialog: Dialog? = null

    fun showErrorDialog(errorMessage: String, onClick : (Int) -> Unit, okButtonText: String = "Ok, got it", checkButtonText: String = "") {
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
        val okButton = dialog!!.findViewById<TextView>(R.id.okButton)
        val checkButton = dialog!!.findViewById<TextView>(R.id.checkButton)
        if (checkButtonText.isNotEmpty()) {
            checkButton.text = checkButtonText
            checkButton.show()
        }
        else {
            checkButton.gone()
        }

        tvNotice.text = errorMessage
        okButton.text = okButtonText
        holderBg.setBackgroundColor(ContextCompat.getColor(activity, R.color.red))
        okButton.setOnClickListener { view: View? ->
            dialog!!.dismiss()
            onClick.invoke(1)
        }

        checkButton.setOnClickListener {
            dialog!!.dismiss()
            onClick.invoke(2)
        }
        dialog!!.show()
    }

    fun showMaintenanceDialog(message: String, onClick : (Int) -> Unit, okButtonText: String = "Ok, got it", checkButtonText: String = "") {
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
        val okButton = dialog!!.findViewById<TextView>(R.id.okButton)
        val checkButton = dialog!!.findViewById<TextView>(R.id.checkButton)
        if (checkButtonText.isNotEmpty()) {
            checkButton.text = checkButtonText
            checkButton.show()
        }
        else {
            checkButton.gone()
        }

        tvNotice.text = message
        okButton.text = okButtonText
        okButton.setOnClickListener { view: View? ->
            dialog!!.dismiss()
            onClick.invoke(1)
        }

        checkButton.setOnClickListener {
            dialog!!.dismiss()
            onClick.invoke(2)
        }
        dialog!!.show()
    }

    fun dismissDialog() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }



}
