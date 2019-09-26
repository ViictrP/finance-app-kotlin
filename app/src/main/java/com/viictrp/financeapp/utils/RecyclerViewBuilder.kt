package com.viictrp.financeapp.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewBuilder {

    private lateinit var recyclerView: RecyclerView

    fun forRecyclerView(rv: RecyclerView): RecyclerViewBuilder {
        this.recyclerView = rv
        return this
    }

    fun withLayoutManager(lm: RecyclerView.LayoutManager): RecyclerViewBuilder {
        this.recyclerView.layoutManager = lm
        return this
    }

    fun <T : RecyclerView.ViewHolder> withAdapter(adapter: RecyclerView.Adapter<T>): RecyclerViewBuilder {
        this.recyclerView.adapter = adapter
        return this
    }

    fun withSwipeHandler(swipeHandler: ItemTouchHelper.SimpleCallback): RecyclerViewBuilder {
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(this.recyclerView)
        return this
    }

    fun build(): RecyclerView {
        return this.recyclerView
    }
}