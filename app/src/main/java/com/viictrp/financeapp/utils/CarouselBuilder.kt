package com.viictrp.financeapp.utils

import android.content.Context
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.viictrp.financeapp.ui.custom.LinePagerIndicatorDecoration
import com.viictrp.financeapp.ui.custom.SnapOnScrollListener
import com.viictrp.financeapp.ui.custom.SnapOnScrollListener.OnItemChangedListener

class CarouselBuilder {

    private lateinit var rv: RecyclerView
    private lateinit var listener: OnItemChangedListener
    private var pageIndicatorDecoration: ItemDecoration? = null

    fun convertToCarousel(
        rv: RecyclerView,
        context: Context,
        onItemChangedListener: OnItemChangedListener
    ): CarouselBuilder {
        this.rv = rv
        this.rv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        this.listener = onItemChangedListener
        return this
    }

    fun withDecoration(pageIndicatorDecoration: ItemDecoration): CarouselBuilder {
        this.pageIndicatorDecoration = pageIndicatorDecoration
        return this
    }

    fun build(): RecyclerView {
        val mSnapHelper = LinearSnapHelper()
        mSnapHelper.attachToRecyclerView(this.rv)
        val snapOnScrollListener = SnapOnScrollListener(
            mSnapHelper,
            SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            listener
        )
        this.rv.addOnScrollListener(snapOnScrollListener)
        this.rv.addItemDecoration(
            if (this.pageIndicatorDecoration != null) this.pageIndicatorDecoration!!
            else LinePagerIndicatorDecoration()
        )
        return this.rv
    }
}