package ro.sts.dgc.ui

import android.content.res.Resources

fun Int.dpToPx() = this * density().toInt()

private fun density() = Resources.getSystem().displayMetrics.density