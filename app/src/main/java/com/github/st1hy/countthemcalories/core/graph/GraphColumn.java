package com.github.st1hy.countthemcalories.core.graph;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.github.st1hy.countthemcalories.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class GraphColumn extends View {
    private static final float[] EMPTY_POINTS = new float[0];

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FLAG_NONE, FLAG_COLUMN, FLAG_LINE, FLAG_ALL})
    public @interface VisibilityFlags {
    }

    public static final int FLAG_NONE = 0;
    public static final int FLAG_COLUMN = 1;
    public static final int FLAG_LINE = 2;
    public static final int FLAG_ALL = 3;

    private Paint columnColor;
    private float progress;
    private RectF columnSize;
    private boolean columnSizeDirty;

    private Orientation orientation;
    private int flags;

    private float[] linePoints = EMPTY_POINTS;
    private Paint linePaint;
    private float[] lineVector = EMPTY_POINTS;
    private boolean lineVectorDirty;

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

    public void setFlags(@VisibilityFlags int flags) {
        if (this.flags != flags) {
            this.flags = flags;
            invalidate();
        }
    }

    @VisibilityFlags
    public int getFlags() {
        return flags;
    }

    public void setProgress(float progress) {
        if (progress < 0f) progress = 0f;
        else if (progress > 1f) progress = 1f;
        this.progress = progress;
        columnSizeDirty = true;
        if (showColumn()) invalidate();
    }

    public void setColumnColor(@ColorInt int color) {
        columnColor.setColor(color);
        if (showColumn()) invalidate();
    }

    public void setLineColor(@ColorInt int color) {
        linePaint.setColor(color);
        if (showLine()) invalidate();
    }

    /**
     * Format [x1, y1, x2, y2, ...]
     * Values need to be within range 0f, 1f. They represent percentage of width and height.
     */
    public void setLinePoints(@NonNull float[] points) {
        if (points.length % 4 != 0) {
            throw new IllegalArgumentException("Illegal points size, must be multiple of 4");
        }
        linePoints = points;
        if (lineVector == null || lineVector.length != linePoints.length) {
            lineVector = new float[linePoints.length];
        }
        lineVectorDirty = true;
        if (showLine()) invalidate();
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GraphColumn, defStyleAttr, R.style.GraphColumn_Default);
        int gc_orientation = a.getInt(R.styleable.GraphColumn_gc_orientation, Orientation.RIGHT_TO_LEFT.gc_orientation);
        int color = a.getColor(R.styleable.GraphColumn_gc_color, Color.GREEN);
        float gc_progress = a.getFloat(R.styleable.GraphColumn_gc_progress, 0.5f);
        int gc_mode = a.getInteger(R.styleable.GraphColumn_gc_mode, 3);
        float gc_lineWidth = a.getDimension(R.styleable.GraphColumn_gc_line_width, 1f);
        int gc_lineColor = a.getColor(R.styleable.GraphColumn_gc_line_color, Color.BLACK);
        a.recycle();
        columnColor = new Paint();
        columnColor.setColor(color);
        progress = gc_progress;
        columnSize = new RectF();
        orientation = Orientation.valueOf(gc_orientation);
        flags = gc_mode;
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(gc_lineWidth);
        linePaint.setColor(gc_lineColor);

        if (isInEditMode()) {
            setLinePoints(new float[] {
                    0f, 0.5f, 0.5f, 1f,
                    0.5f, 1f, 1f, 0.5f
            });
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        columnSizeDirty = true;
        lineVectorDirty = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showColumn()) {
            if (columnSizeDirty) setColumnSizeBounds();
            canvas.drawRect(columnSize, columnColor);
        }
        if (showLine()) {
            if (lineVectorDirty) setLineVector();
            canvas.drawLines(lineVector, linePaint);
        }
    }

    private void setColumnSizeBounds() {
        float width = getWidth();
        float height = getHeight();
        switch (orientation) {
            case RIGHT_TO_LEFT:
                columnSize.set((1f - progress) * width, 0, width, height);
                break;
            case LEFT_TO_RIGHT:
                columnSize.set(0f, 0f, progress * width, height);
                break;
            case TOP_TO_BOTTOM:
                columnSize.set(0f, 0f, width, progress * height);
                break;
            case BOTTOM_TO_TOP:
                columnSize.set(0f, (1f - progress) * height, width, height);
                break;
        }
        columnSizeDirty = false;
    }

    private void setLineVector() {
        float width = getWidth();
        float height = getHeight();
        for (int i = 0; i < linePoints.length; i+=2) {
            float x = linePoints[i];
            float y = linePoints[i+1];
            float dx, dy;
            switch (orientation) {
                case RIGHT_TO_LEFT:
                    dx = (1f - y) * width;
                    dy = x * height;
                    break;
                case LEFT_TO_RIGHT:
                    dx = y * width;
                    dy = x * height;
                    break;
                case TOP_TO_BOTTOM:
                    dx = x * width;
                    dy = y * height;
                    break;
                case BOTTOM_TO_TOP:
                    dx = x * width;
                    dy = (1f - y) * height;
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            lineVector[i] = dx;
            lineVector[i+1] = dy;
        }
        lineVectorDirty = false;
    }

    private boolean showColumn() {
        return (flags & FLAG_COLUMN) > 0;
    }

    private boolean showLine() {
        return (flags & FLAG_LINE) > 0;
    }

    private enum Orientation {
        RIGHT_TO_LEFT(1), LEFT_TO_RIGHT(2), TOP_TO_BOTTOM(3), BOTTOM_TO_TOP(4);

        private final int gc_orientation;

        Orientation(int gc_orientation) {
            this.gc_orientation = gc_orientation;
        }

        @NonNull
        public static Orientation valueOf(int gc_orientation) throws IllegalArgumentException {
            for (Orientation o : values()) {
                if (o.gc_orientation == gc_orientation) return o;
            }
            throw new IllegalArgumentException("Unknown orientation value");
        }
    }
}
