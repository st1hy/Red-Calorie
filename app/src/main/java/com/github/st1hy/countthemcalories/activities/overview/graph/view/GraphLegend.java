package com.github.st1hy.countthemcalories.activities.overview.graph.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import rx.subscriptions.CompositeSubscription;

public class GraphLegend extends View {

    GraphLegendModel model;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    public GraphLegend(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public GraphLegend(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public GraphLegend(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GraphLegend(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        model = new GraphLegendModel(context, attrs, defStyleAttr);
    }

    public GraphLegendModel getModel() {
        return model;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        subscriptions.add(model.invalidated().subscribe(any -> invalidate()));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        subscriptions.clear();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        model.setDirty();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = canvas.getWidth();
        float height = canvas.getHeight();
        model.updateLegendVector(width, height);
        canvas.drawLines(model.lineVector, model.linePaint);
    }
}
