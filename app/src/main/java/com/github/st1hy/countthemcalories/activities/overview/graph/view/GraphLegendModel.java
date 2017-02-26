package com.github.st1hy.countthemcalories.activities.overview.graph.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.model.TimePeriod;

import rx.Observable;
import rx.subjects.PublishSubject;

import static com.github.st1hy.countthemcalories.activities.overview.graph.view.GraphColumnModel.FLAG_ALL;
import static com.github.st1hy.countthemcalories.activities.overview.graph.view.GraphColumnModel.FLAG_COLUMN;
import static com.github.st1hy.countthemcalories.activities.overview.graph.view.GraphColumnModel.FLAG_LINE;

public class GraphLegendModel {

    private static final float[] EMPTY_POINTS = new float[0];
    float[] lineVector = EMPTY_POINTS;
    final Paint linePaint;

    private boolean isDirty = false;
    private int flags;
    private final PublishSubject<Void> invalidate = PublishSubject.create();
    private final float minLineSpaces;
    private float[] autoScale = EMPTY_POINTS;
    private boolean isAutoScaleDirty = false;
    private final Orientation orientation;
    private TimePeriod timePeriod;

    GraphLegendModel(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GraphColumn, defStyleAttr, R.style.GraphColumn_Default);
        int gc_legend_line_color = a.getColor(R.styleable.GraphColumn_gc_legend_line_overlay_color, Color.BLACK);
        float gc_legend_line_width = a.getDimensionPixelSize(R.styleable.GraphColumn_gc_legend_line_width, 1);
        int gc_mode = a.getInteger(R.styleable.GraphColumn_gc_mode, FLAG_ALL);
        minLineSpaces = a.getDimension(R.styleable.GraphColumn_gc_legend_min_legend_line_space, 100);
        int gc_orientation = a.getInt(R.styleable.GraphColumn_gc_orientation, Orientation.RIGHT_TO_LEFT.gc_orientation);
        a.recycle();

        this.flags = gc_mode;
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(gc_legend_line_width);
        linePaint.setColor(gc_legend_line_color);
        orientation = Orientation.valueOf(gc_orientation);
    }

    public void updateData(@NonNull TimePeriod timePeriod) {
        this.timePeriod = timePeriod;
        isAutoScaleDirty = true;
        setDirty();
        invalidate();
    }

    @NonNull
    @CheckResult
    Observable<Void> invalidated() {
        return invalidate;
    }

    private void invalidate() {
        invalidate.onNext(null);
    }

    void setDirty() {
        isDirty = true;
    }

    void updateLegendVector(float width, float height) {
        if (!isDirty || timePeriod == null) return;
        setAutoScale(width, height);
        for (int i = 0; i < autoScale.length; i++) {
            int x = i * 4;
            int y = x + 1;
            float sizeNormal = timePeriod.normalizeDayValue(autoScale[i]);
            switch (orientation) {
                case RIGHT_TO_LEFT:
                case LEFT_TO_RIGHT:
                    float curWidth = width * sizeNormal;
                    if (orientation == Orientation.RIGHT_TO_LEFT) {
                        curWidth = width - curWidth;
                    }
                    lineVector[x] = curWidth;
                    lineVector[y] = 0f;
                    lineVector[x+2] = curWidth;
                    lineVector[y+2] = height;
                    break;
                case TOP_TO_BOTTOM:
                case BOTTOM_TO_TOP:
                    float curHeight = height * sizeNormal;
                    if (orientation == Orientation.BOTTOM_TO_TOP) {
                        curHeight = height - curHeight;
                    }
                    lineVector[x] = 0f;
                    lineVector[y] = curHeight;
                    lineVector[x+2] = width;
                    lineVector[y+2] = curHeight;
                    break;
            }
        }
        isDirty = false;
    }

    private void setAutoScale(float width, float height) {
        if (!isAutoScaleDirty || timePeriod == null) return;
        float totalViewSize;
        switch (orientation) {
            case RIGHT_TO_LEFT:
            case LEFT_TO_RIGHT:
                totalViewSize = width;
                break;
            case TOP_TO_BOTTOM:
            case BOTTOM_TO_TOP:
                totalViewSize = height;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        autoScale = timePeriod.getAutoScaleLines().sensibleScale(minLineSpaces, totalViewSize);
        int newLineSize = autoScale.length * 4;
        if (lineVector.length != newLineSize) {
            lineVector = new float[newLineSize];
        }
        isAutoScaleDirty = false;
    }

    boolean showColumn() {
        return (flags & FLAG_COLUMN) > 0;
    }

    boolean showLine() {
        return (flags & FLAG_LINE) > 0;
    }
}

