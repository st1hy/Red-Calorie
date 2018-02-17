package com.github.st1hy.countthemcalories.database.rx;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.contract.Schedulers;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;

public abstract class AbstractRxDatabaseModel {
    @Inject
    Lazy<DaoSession> session;
    @Inject
    Schedulers scheduler;

    @NonNull
    public DaoSession session() {
        return session.get();
    }

    @NonNull
    public <R> Observable<R> fromDatabaseTask(@NonNull Callable<R> task) {
        return Observable.fromCallable(callInTx(task))
                .subscribeOn(scheduler.io());
    }

    @NonNull
    private <R> Callable<R> callInTx(@NonNull final Callable<R> task) {
        return () -> session().callInTx(task);
    }
}
