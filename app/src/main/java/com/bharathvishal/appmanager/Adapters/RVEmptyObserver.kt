package com.bharathvishal.appmanager.Adapters

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Bharath Vishal on 26-06-2018.
 * Recyclerview EmptyObserver
 */
class RVEmptyObserver
/**
 * Constructor to set an Empty View for the RV
 */
(
        private val recyclerView: RecyclerView, private val emptyView: View?) : RecyclerView.AdapterDataObserver() {


    init {
        checkIfEmpty()
    }


    /**
     * Check if Layout is empty and show the appropriate view
     */
    private fun checkIfEmpty() {
        if (emptyView != null && recyclerView.adapter != null) {
            val emptyViewVisible = recyclerView.adapter.itemCount == 0
            emptyView.visibility = if (emptyViewVisible) View.VISIBLE else View.GONE
            recyclerView.visibility = if (emptyViewVisible) View.GONE else View.VISIBLE
        }
    }


    /**
     * Abstract method implementations
     */
    override fun onChanged() {
        checkIfEmpty()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        checkIfEmpty()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        checkIfEmpty()
    }

}