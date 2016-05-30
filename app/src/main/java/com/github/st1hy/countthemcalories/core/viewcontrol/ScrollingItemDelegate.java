package com.github.st1hy.countthemcalories.core.viewcontrol;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.jakewharton.rxbinding.internal.Functions;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.view.ViewScrollChangeEvent;

import dagger.internal.Preconditions;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class ScrollingItemDelegate {

    private final Action1<Void> resetScrollOnPreDraw = new Action1<Void>() {
        @Override
        public void call(Void aVoid) {
            resetScroll();
        }
    };
    private final Action1<ViewScrollChangeEvent> scrollToNearest = new Action1<ViewScrollChangeEvent>() {
        @Override
        public void call(ViewScrollChangeEvent viewScrollChangeEvent) {
            onFinishedScroll(viewScrollChangeEvent.scrollX());
        }
    };
    private final Action1<Void> scrollToContent = new Action1<Void>() {
        @Override
        public void call(Void aVoid) {
            scrollView.smoothScrollTo(center.getLeft(), 0);
        }
    };
    private final HorizontalScrollObservable scrollingObservable;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    final View left, center, right;
    final HorizontalScrollView scrollView;

    private ScrollingItemDelegate(@NonNull Builder builder) {
        this.scrollView = builder.scrollView;
        this.left = builder.left;
        this.center = builder.center;
        this.right = builder.right;
        this.scrollingObservable = HorizontalScrollObservable.create(scrollView);
    }

    /**
     * Changes width of center view to match parent.
     */
    public void fillParent(@NonNull final ViewGroup parent) {
        int parentWidth = parent.getWidth();
        ViewGroup.LayoutParams layoutParams = center.getLayoutParams();
        layoutParams.width = parentWidth;
        center.setLayoutParams(layoutParams);
    }


    public void onAttached() {
        resetScroll();
        subscribe(scrollingObservable.getScrollToPositionObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scrollToNearest));
        subscribe(scrollingObservable.getIdleObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scrollToContent));
    }

    public void onDetached() {
        subscriptions.clear();
        resetScroll();
    }

    /***
     * Add subscription to be managed by this delegate. It will be automatically unsubscribed in {@link #onDetached()}
     */
    public void subscribe(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }

    private void resetScroll() {
        if (center.getLeft() != 0) {
            resetScrollInternal();
        } else {
            subscriptions.add(RxView.preDraws(center, Functions.FUNC0_ALWAYS_TRUE)
                    .take(1)
                    .subscribe(resetScrollOnPreDraw));
        }
    }

    private void resetScrollInternal() {
        scrollView.scrollTo(center.getLeft(), 0);
    }

    private void onFinishedScroll(int scrollX) {
        int toDelete = Math.abs(scrollX - left.getLeft());
        int toContent = Math.abs(scrollX - center.getLeft());
        int toEdit = Math.abs(scrollX + center.getWidth() - right.getRight());
        if (toContent <= toDelete && toContent <= toEdit) {
            if (toContent > 0) scrollView.smoothScrollTo(center.getLeft(), 0);
        } else if (toDelete <= toEdit) {
            if (toDelete > 0) scrollView.smoothScrollTo(left.getLeft(), 0);
        } else {
            if (toEdit > 0) scrollView.smoothScrollTo(right.getLeft(), 0);
        }
    }

    public static class Builder {
        View left, center, right;
        HorizontalScrollView scrollView;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        @NonNull
        public Builder setLeft(@NonNull View left) {
            this.left = left;
            return this;
        }

        @NonNull
        public Builder setCenter(@NonNull View center) {
            this.center = center;
            return this;
        }

        @NonNull
        public Builder setRight(@NonNull View right) {
            this.right = right;
            return this;
        }

        @NonNull
        public Builder setScrollView(@NonNull HorizontalScrollView scrollView) {
            this.scrollView = scrollView;
            return this;
        }

        public ScrollingItemDelegate build() {
            Preconditions.checkNotNull(left);
            Preconditions.checkNotNull(center);
            Preconditions.checkNotNull(right);
            Preconditions.checkNotNull(scrollView);
            return new ScrollingItemDelegate(this);
        }
    }
}
