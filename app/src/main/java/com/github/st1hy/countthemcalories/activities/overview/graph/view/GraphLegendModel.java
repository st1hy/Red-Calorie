package com.github.st1hy.countthemcalories.activities.overview.graph.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.github.st1hy.countthemcalories.R;

public class GraphLegendModel {

    private static final float[] EMPTY_POINTS = new float[0];
    float[] lineVector = EMPTY_POINTS;
    final Paint linePaint;

    private boolean isDirty = false;

    GraphLegendModel(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GraphColumn, defStyleAttr, R.style.GraphColumn_Default);
        int gc_legend_line_color = a.getColor(R.styleable.GraphColumn_gc_legend_line_color, Color.BLACK);
        float gc_legend_line_width = a.getDimensionPixelSize(R.styleable.GraphColumn_gc_legend_line_width, 1);
        a.recycle();

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(gc_legend_line_width);
        linePaint.setColor(gc_legend_line_color);
    }

    void setDirty() {
        isDirty = true;
    }

    void setEditData(float width, float height) {
        if (!isDirty) return;
        lineVector = new float[]{
                0f, 0f, width, height,
                width, 0f, 0f, height
        };
        isDirty = false;
    }

}

