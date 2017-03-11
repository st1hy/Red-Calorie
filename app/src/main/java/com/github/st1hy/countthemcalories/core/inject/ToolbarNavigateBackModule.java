package com.github.st1hy.countthemcalories.core.inject;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.baseview.PressBackOnClickListener;
import com.google.common.base.Preconditions;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class ToolbarNavigateBackModule {

    @Provides
    public static Toolbar toolbar(AppCompatActivity activity, PressBackOnClickListener onClickListener) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.image_header_toolbar);
        activity.setSupportActionBar(toolbar);
        Preconditions.checkNotNull(activity.getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(onClickListener);
        return toolbar;
    }
}
