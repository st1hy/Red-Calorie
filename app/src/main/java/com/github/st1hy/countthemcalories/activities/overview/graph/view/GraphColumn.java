package com.github.st1hy.countthemcalories.activities.overview.graph.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import rx.subscriptions.CompositeSubscription;

public class GraphColumn extends View {

    private GraphColumnModel model;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    public GraphColumn(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public GraphColumn(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public GraphColumn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GraphColumn(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        model = new GraphColumnModel(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            model.setLinePoints(new float[]{
                    0f, 0.5f, 0.5f, 1f,
                    0.5f, 1f, 1f, 0.5f
            });
            model.setPoint(0.75f, 0f, 1f);
            model.setValue(1f, 2f);
        }
    }

    public GraphColumnModel getModel() {
        return model;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        model.setDirty();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getHeight();
        canvas.drawColor(model.backgroundColor);
        if (model.showColumn()) {
            model.setColumnSizeBounds(width, height);
            canvas.drawRect(model.columnSize, model.columnColor);
            canvas.drawText(model.textValue, model.textValuePosition.x, model.textValuePosition.y,
                    model.textValuePaint);
        }
        if (model.showLine()) {
            model.setLineVector(width, height);
            canvas.drawLines(model.lineVector, model.linePaint);
        }
        if (model.showPoint()) {
            model.setPointSize(width, height);
            canvas.drawRect(model.pointSizeBounds, model.pointColor);
            canvas.drawText(model.textPointValue, model.pointValuePosition.x, model.pointValuePosition.y,
                    model.pointValuePaint);
        }
        if (model.isSelected) {
            model.setSelection(width, height);
            canvas.drawLines(model.selectionLine, model.selectedPaint);
        }
        model.setLegendBounds(width, height);
        canvas.drawLines(model.legendVector, model.columnLegendLinePaint);
        canvas.drawLines(model.rowLegendVector, model.rowLegendLinePaint);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        subscriptions.add(
                model.invalidated().subscribe(any -> invalidate())
        );
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        subscriptions.clear();
    }
}
