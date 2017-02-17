package com.github.st1hy.countthemcalories.activities.overview.meals.presenter;

import android.view.MotionEvent;

import com.github.st1hy.countthemcalories.activities.overview.meals.view.OverviewView;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

@PerFragment
public class FabMenuDeactivation implements BasicLifecycle {

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    OverviewView view;

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
