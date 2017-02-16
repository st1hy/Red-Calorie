package com.github.st1hy.countthemcalories.core.rx;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.getbase.floatingactionbutton.FloatingActionsMenu.OnFloatingActionsMenuUpdateListener;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public final class RxFabMenu {

    private RxFabMenu() {
    }

    @NonNull
    @CheckResult
    public static Observable<Boolean> menuIsOpen(@NonNull FloatingActionsMenu menu) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                menu.setOnFloatingActionsMenuUpdateListener(new OnFloatingActionsMenuUpdateListener() {
                    @Override
                    public void onMenuExpanded() {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(true);
                        }
                    }

                    @Override
                    public void onMenuCollapsed() {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(false);
                        }
                    }
                });
                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        menu.setOnFloatingActionsMenuUpdateListener(null);
                    }
                });
            }
        });
    }
}
