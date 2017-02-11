package com.github.st1hy.countthemcalories.core.rx;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.DaoSession;

import java.util.concurrent.Callable;

import dagger.Lazy;
import rx.Observable;

public abstract class AbstractRxDatabaseModel {
    private final Lazy<DaoSession> session;

    public AbstractRxDatabaseModel(@NonNull Lazy<DaoSession> session) {
        this.session = session;
    }

    @NonNull
    public DaoSession session() {
        return session.get();
    }

    @NonNull
    public <R> Observable<R> fromDatabaseTask(@NonNull Callable<R> task) {
        return Observable.fromCallable(callInTx(task))
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    private <R> Callable<R> callInTx(@NonNull final Callable<R> task) {
        return () -> session().callInTx(task);
    }
}
