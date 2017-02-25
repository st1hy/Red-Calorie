package com.github.st1hy.countthemcalories.activities.overview.graph.model;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.inject.PerActivity;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;

@PerActivity
public class GraphGlobalViewModel {

    private int[] mainAxisMargins = new int[2];

    private final PublishSubject<int[]> mainAxisMarginsSubject = PublishSubject.create();

    @Inject
    public GraphGlobalViewModel() {
    }

    public void setMainAxisMargins(int leftMargin, int bottomMargin) {
        if (leftMargin != mainAxisMargins[0] || bottomMargin != mainAxisMargins[1]) {
            mainAxisMargins = new int[] {leftMargin, bottomMargin};
            mainAxisMarginsSubject.onNext(mainAxisMargins);
        }
    }

    @CheckResult
    @NonNull
    public Observable<int[]> mainAxisMargins() {
        return Observable.just(mainAxisMargins)
                .mergeWith(mainAxisMarginsSubject)
                .distinctUntilChanged();
    }

}
