/**
 *
 * Copyright 2018-2023 Bharath Vishal G.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **/

package com.bharathvishal.appmanager.Adapters

import android.view.View

/**
 * Created by Bharath Vishal on 26-06-2018.
 * Recyclerview EmptyObserver
 */
@Suppress("PackageName")
class RVEmptyObserver
/**
 * Constructor to set an Empty View for the RV
 */
(
        private val recyclerView: androidx.recyclerview.widget.RecyclerView, private val emptyView: View?) : androidx.recyclerview.widget.RecyclerView.AdapterDataObserver() {


    init {
        checkIfEmpty()
    }


    /**
     * Check if Layout is empty and show the appropriate view
     */
    private fun checkIfEmpty() {
        if (emptyView != null && recyclerView.adapter != null) {
            val emptyViewVisible = recyclerView.adapter!!.itemCount == 0
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