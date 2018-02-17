package com.github.st1hy.countthemcalories.ui.activities.overview.graph.view

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.st1hy.countthemcalories.ui.activities.overview.graph.inject.column.GraphColumnRootView
import com.github.st1hy.countthemcalories.ui.activities.overview.graph.inject.column.GraphItem
import com.github.st1hy.countthemcalories.ui.inject.quantifier.context.ActivityContext
import kotlinx.android.synthetic.main.overview_graph_item.view.*
import rx.Observable
import rx.subjects.PublishSubject
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject


class GraphColumnViewHolder @Inject constructor(
        @GraphColumnRootView itemView: View
) : RecyclerView.ViewHolder(itemView) {

    @Inject @ActivityContext lateinit var context: Context
    @Inject @GraphItem lateinit var positionClicked: PublishSubject<Int>

    private val model = itemView.graph_item_column.model
    val mutable2SegmentLine = FloatArray(8)
    private var _position = -1
    private val subscriptions = CompositeSubscription()

    init {
        itemView.graph_item_column.setOnClickListener {
            if (_position != -1) positionClicked.onNext(_position)
        }
    }

    fun setName(name: String) {
        itemView.graph_item_name!!.text = name
    }

    fun setValue(value: Float, maxValue: Float) {
        model.setValue(value, maxValue)
    }

    fun setWeight(value: Float, min: Float, max: Float) {
        model.setPoint(value, min, max)
    }

    fun setWeightVisibility(isVisible: Boolean) {
        setFlag(isVisible, GraphColumnModel.FLAG_LINE)
        setFlag(isVisible, GraphColumnModel.FLAG_POINTS)
    }

    fun setValueVisibility(isVisible: Boolean) {
        setFlag(isVisible, GraphColumnModel.FLAG_COLUMN)
    }

    private fun setFlag(isEnabled: Boolean, @GraphColumnModel.VisibilityFlags flag: Int) {
        @GraphColumnModel.VisibilityFlags var flags = model.flags
        if (isEnabled) {
            flags = flags or flag
        } else {
            if (flags and flag > 0)
                flags -= flag
        }
        model.flags = flags
    }

    fun setLine(points: FloatArray) {
        model.setLinePoints(points)
    }

    fun setPos(position: Int) {
        this._position = position
    }

    fun setColor(@ColorRes colorRes: Int) {
        val color = ContextCompat.getColor(context, colorRes)
        model.setColumnColor(color)
    }

    fun setBackground(@ColorRes colorRes: Int) {
        val color = ContextCompat.getColor(context, colorRes)
        model.setBackgroundColor(color)
    }

    fun onViewAttached(positionObservable: Observable<Int>) {
        subscriptions.addAll(
                positionObservable.subscribe { position ->
                    this.onPositionSelected(position)
                }
        )
    }

    fun onViewDetached() {
        subscriptions.clear()
    }

    private fun onPositionSelected(selectedPosition: Int) {
        val isSelected = _position == selectedPosition
        model.setSelected(isSelected)
    }
}