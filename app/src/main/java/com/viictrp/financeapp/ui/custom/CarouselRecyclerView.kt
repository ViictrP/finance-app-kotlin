package com.viictrp.financeapp.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import com.viictrp.financeapp.R
import com.viictrp.financeapp.ui.custom.SnapOnScrollListener.Behavior
import kotlin.math.pow

class CarouselRecyclerView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {

    private lateinit var mItemChangedListener: OnItemChangedListener
    private lateinit var mSnapHelper: LinearSnapHelper

    private val activeColor
            by lazy { ContextCompat.getColor(context, R.color.white) }
    private val inactiveColor
            by lazy { ContextCompat.getColor(context, R.color.labelTintColor) }
    private var viewsToChangeColor = listOf<Int>()

    fun <T : ViewHolder> initialize(newAdapter: Adapter<T>, listener: OnItemChangedListener) {
        this.mItemChangedListener = listener
        layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        this.mSnapHelper = LinearSnapHelper()
        this.mSnapHelper.attachToRecyclerView(this)
        val snapOnScrollListener = SnapOnScrollListener(
            this.mSnapHelper,
            Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            this.mItemChangedListener
        )
        newAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                post {
                    if (childCount > 0) {
                        val sidePadding = (width / 2) - (getChildAt(0).width / 2)
                        setPadding(sidePadding, 0, sidePadding, 0)
                        scrollToPosition(0)
                        // onScrollChanged()
                        addOnScrollListener(snapOnScrollListener)
                    }
                }
            }
        })
        adapter = newAdapter
    }

    private fun onScrollChanged() {
        post {
            (0 until childCount).forEach { position ->
                val child = getChildAt(position)
                val childCenterX = (child.left + child.right) / 2
                val scaleValue = getGaussianScale(childCenterX, 1f, 1f, 150.toDouble())
                child.scaleX = scaleValue
                child.scaleY = scaleValue
                colorView(child, scaleValue)
            }
        }
    }

    fun setViewsToChangeColor(viewIds: List<Int>) {
        viewsToChangeColor = viewIds
    }

    @SuppressLint("RestrictedApi")
    private fun colorView(child: View, scaleValue: Float) {
        val saturationPercent = (scaleValue - 1) / 1f
        val alphaPercent = scaleValue / 2f
        val matrix = ColorMatrix()
        matrix.setSaturation(saturationPercent)

        viewsToChangeColor.forEach { viewId ->
            when (val viewToChangeColor = child.findViewById<View>(viewId)) {
                is ImageView -> {
                    viewToChangeColor.colorFilter = ColorMatrixColorFilter(matrix)
                    viewToChangeColor.imageAlpha = (255 * alphaPercent).toInt()
                }
                is TextView -> {
                    val textColor = ArgbEvaluator().evaluate(
                        saturationPercent,
                        inactiveColor,
                        activeColor
                    ) as Int
                    viewToChangeColor.setTextColor(textColor)
                }
            }
        }
    }

    private fun getGaussianScale(
        childCenterX: Int,
        minScaleOffest: Float,
        scaleFactor: Float,
        spreadFactor: Double
    ): Float {
        val recyclerCenterX = (left + right) / 2
        return (Math.E.pow(
            -(childCenterX - recyclerCenterX.toDouble()).pow(2.toDouble()) / (2 * spreadFactor.pow(2.toDouble()))
        ) * scaleFactor + minScaleOffest).toFloat()
    }

    interface OnItemChangedListener {

        fun onItemChangedListener(position: Int)
    }
}