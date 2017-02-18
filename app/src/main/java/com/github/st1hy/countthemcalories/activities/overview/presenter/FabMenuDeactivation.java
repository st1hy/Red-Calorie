package com.github.st1hy.countthemcalories.activities.overview.presenter;

import android.view.MotionEvent;

import com.github.st1hy.countthemcalories.activities.overview.view.OverviewScreen;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

@PerActivity
public class FabMenuDeactivation implements BasicLifecycle {

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    OverviewScreen view;

    @Inject
    public FabMenuDeactivation() {
    }

    @Override
    public void onStart() {
        subscriptions.add(
                view.touchOverlay(event -> view.isFabMenuOpen() && event.getAction() == MotionEvent.ACTION_DOWN)
                        .subscribe(ignoreEvent -> view.closeFloatingMenu())
        );
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }
}
