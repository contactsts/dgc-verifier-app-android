package ro.sts.dgc.ui

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import ro.sts.dgc.R

class AppUpdateReadyDialog(context: Context) : AlertDialog(context) {

    private var appUpdateConfirmClickListener: View.OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_app_update_ready)
        window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawableResource(R.drawable.bg_dialog)
        }
        findViewById<TextView>(R.id.app_update_dialog_ok_button)?.apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener { v: View? ->
                dismiss()
                appUpdateConfirmClickListener?.onClick(v)
            }
        }
        findViewById<View>(R.id.app_update_dialog_close_button)?.setOnClickListener { _ -> cancel() }
    }

    fun setAppUpdateConfirmClickListener(listener: View.OnClickListener?) {
        appUpdateConfirmClickListener = listener
    }

}