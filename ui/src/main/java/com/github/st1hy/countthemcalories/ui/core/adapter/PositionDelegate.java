package com.github.st1hy.countthemcalories.ui.core.adapter;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class PositionDelegate {

    private final AtomicInteger atomicPosition = new AtomicInteger(-1);
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    Observable<RecyclerEvent> eventObservable;

    @Inject
    public PositionDelegate() {
    }

    public void set(int position) {
        atomicPosition.set(position);
    }

    public int get() {
        return atomicPosition.get();
    }


    public void onAttached() {
        subscriptions.add(eventObservable.subscribe(recyclerEvent -> {
            int eventPosition = recyclerEvent.getPosition();
            switch (recyclerEvent.getType()) {
                case ADDED:
                    if (get() >= eventPosition) atomicPosition.incrementAndGet();
                    break;
                case REMOVED:
                    if (get() > eventPosition) atomicPosition.decrementAndGet();
                    break;
            }
        }));
    }

    public void onDetached() {
        subscriptions.clear();
    }

}
