package com.github.st1hy.countthemcalories.database.parcel;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.database.DaoSession;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public abstract class DaoParcel<T> implements Parcelable {
    protected T dao;
    protected final WhenReady<T, DaoSession> daoWhenReady;

    public DaoParcel(@NonNull final T dao) {
        this.dao = dao;
        this.daoWhenReady = new JustNow<>(dao);
    }

    protected DaoParcel(@NonNull WhenReady<T, DaoSession> daoWhenReady) {
        this.daoWhenReady = daoWhenReady;
    }

    @NonNull
    public WhenReady<T, DaoSession> getWhenReady() {
        return daoWhenReady;
    }

    public interface WhenReady<R, Q> {

        @Nullable
        R getOrNull();

        Observable<R> get(@NonNull Q arg);
    }

    static class JustNow<R> implements WhenReady<R, DaoSession> {
        final R data;

        public JustNow(@NonNull R data) {
            this.data = data;
        }

        @Nullable
        @Override
        public R getOrNull() {
            return data;
        }

        @Override
        public Observable<R> get(@NonNull DaoSession aVoid) {
            return Observable.just(data);
        }
    }

    protected static abstract class ReadFromDb<R> implements WhenReady<R, DaoSession> {
        protected R dao;
        protected final long id;

        public ReadFromDb(long id) {
            this.id = id;
        }

        @Nullable
        @Override
        public R getOrNull() {
            return dao;
        }

        @Override
        public Observable<R> get(@NonNull DaoSession session) {
            if (dao != null) {
                return Observable.just(dao);
            } else return Observable
                    .create(readDao(session, id))
                    .doOnNext(new Action1<R>() {
                        @Override
                        public void call(R r) {
                            dao = r;
                        }
                    });
        }

        protected abstract SessionReader<R> readDao(@NonNull DaoSession session, long id);
    }


    protected static class SessionReader<R> implements Observable.OnSubscribe<R>, Callable<R> {
        private final DaoSession session;
        private final Class<R> daoClass;
        private final long id;

        public SessionReader(@NonNull DaoSession session, Class<R> daoClass, long id) {
            this.session = session;
            this.daoClass = daoClass;
            this.id = id;
        }

        @Override
        public void call(Subscriber<? super R> subscriber) {
            if (!subscriber.isUnsubscribed()) {
                try {
                    subscriber.onNext(session.callInTx(this));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }

        @Override
        public R call() throws Exception {
            return session.load(daoClass, id);
        }
    }
}
