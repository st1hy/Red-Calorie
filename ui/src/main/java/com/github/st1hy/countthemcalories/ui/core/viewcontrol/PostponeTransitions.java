package com.github.st1hy.countthemcalories.ui.core.viewcontrol;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.github.st1hy.countthemcalories.ui.core.Utils;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;

public class PostponeTransitions {

    @Inject
    Activity activity;
    @Inject
    Utils utils;

    @Inject
    public PostponeTransitions() {
    }

    @SuppressLint("NewApi")
    public Subscription postponeTransitions(Observable<?> trigger) {
        if (utils.hasLollipop()) {
            activity.postponeEnterTransition();
            return trigger.subscribe(any -> startPostponedTransitions());
        }
        return new SubscriptionStub();
    }

    @SuppressLint("NewApi")
    private void startPostponedTransitions() {
        activity.startPostponedEnterTransition();
    }

    private static class SubscriptionStub implements Subscription {
        private boolean isUnsubscribed;

        @Override
        public void unsubscribe() {
            isUnsubscribed = true;
        }

        @Override
        public boolean isUnsubscribed() {
            return isUnsubscribed;
        }
    }
}
