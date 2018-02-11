package com.github.st1hy.countthemcalories.ui.core.adapter.layoutmanager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

public class HorizontalLayoutManager extends LinearLayoutManager {

    public HorizontalLayoutManager(Context context) {
        super(context);
        init();
    }

    public HorizontalLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        init();
    }

    public HorizontalLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(LinearLayoutManager.HORIZONTAL);
    }
}
