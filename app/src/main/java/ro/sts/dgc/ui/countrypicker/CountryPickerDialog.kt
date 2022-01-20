package ro.sts.dgc.ui.countrypicker

import android.transition.Fade
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import ro.sts.dgc.R

internal class CountryPickerDialog(
    countryPicker: CountryPicker,
    countries: List<Country>
) {
    private val context = countryPicker.context
    private val attrs = countryPicker.attrs

    private val dialog: AlertDialog = AlertDialog.Builder(context).apply {
        setCancelable(true)
        setView(R.layout.dialog_country_picker)
    }.show()

    private val titleView = dialog.findViewById<TextView>(R.id.title)!!
    private val searchView = dialog.findViewById<EditText>(R.id.search)!!
    private val clearSearchView = dialog.findViewById<View>(R.id.clearSearch)!!
    private val recyclerView = dialog.findViewById<RecyclerView>(R.id.countryList)!!
    private val emptyView = dialog.findViewById<TextView>(R.id.emptyResults)!!

    private val parentView = titleView.parent as ViewGroup

    private val countryAdapter = CountryAdapter(countryPicker, countries, attrs) { newList ->
        recyclerView.isVisible = newList.isNotEmpty()
        emptyView.isVisible = newList.isEmpty()
    }.apply {
        setHasStableIds(true)
    }

    private val clearSearchTransition = Fade().apply {
        addTarget(clearSearchView)
    }

    private val cancelRunnable = CancelSearchRunnable()

    init {
        with(titleView) {
            isVisible = attrs.dialogShowTitle
            text = attrs.dialogTitle
        }
        with(searchView) {
            isVisible = attrs.dialogShowSearch
            hint = attrs.dialogSearchHint

            if (attrs.dialogShowSearch) {
                observeSearch()
            }
        }
        clearSearchView.setOnClickListener {
            searchView.setText("")
        }
        with(recyclerView) {
            post {
                layoutManager = LinearLayoutManager(context)
                adapter = countryAdapter
            }
        }
        emptyView.text = attrs.dialogEmptyViewText
    }

    fun close() {
        dialog.cancel()
    }

    private fun EditText.observeSearch() {
        addTextChangedListener {
            removeCallbacks(cancelRunnable)

            val search = it?.trim() ?: ""

            val newClearSearchVisibility = search.isNotBlank()
            if (newClearSearchVisibility != clearSearchView.isVisible) {
                TransitionManager.beginDelayedTransition(parentView, clearSearchTransition)
                clearSearchView.isVisible = newClearSearchVisibility
            }

            postDelayed(
                SearchRunnable {
                    countryAdapter.updateSearch(search)
                }, 500
            )
        }
    }

    private class SearchRunnable(
        private val updateSearch: () -> Unit
    ) : Runnable {
        override fun run() {
            updateSearch()
        }

        override fun hashCode(): Int = 0
        override fun equals(other: Any?): Boolean = other is SearchRunnable || other is CancelSearchRunnable
    }

    private class CancelSearchRunnable : Runnable {
        override fun run() {}
        override fun hashCode(): Int = 0
        override fun equals(other: Any?): Boolean = other is SearchRunnable || other is CancelSearchRunnable
    }
}

private fun RecyclerView.smoothSnapToPosition(position: Int, snapMode: Int = LinearSmoothScroller.SNAP_TO_START) {
    val smoothScroller = object : LinearSmoothScroller(this.context) {
        override fun getVerticalSnapPreference(): Int {
            return snapMode
        }

        override fun getHorizontalSnapPreference(): Int {
            return snapMode
        }
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)
}