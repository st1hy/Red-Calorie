package com.github.st1hy.countthemcalories.core.rx;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.adapter.SearchableDatabase;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.google.common.base.Strings;

import java.util.concurrent.Callable;

import dagger.Lazy;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.CursorQuery;
import rx.Observable;

public abstract class RxDatabaseModel<T> implements SearchableDatabase {
    private final Lazy<DaoSession> session;
    private CursorQuery allSortedByNameQuery;
    private CursorQuery filteredSortedByNameQuery;
    protected String lastFilter = "";

    public RxDatabaseModel(@NonNull Lazy<DaoSession> session) {
        this.session = session;
    }

    /**
     * Reads tag entity which is NOT cached or attached to dao into output
     */
    public abstract void performReadEntity(@NonNull Cursor cursor, @NonNull T output);

    @NonNull
    public Observable<T> getById(long id) {
        return fromDatabaseTask(fromId(id));
    }

    @NonNull
    public Observable<Cursor> getAllFiltered(@NonNull String partOfName) {
        return fromDatabaseTask(filtered(partOfName));
    }

    @NonNull
    public Observable<Cursor> addNewAndRefresh(@NonNull T data) {
        return fromDatabaseTask(addNewAndRefreshCall(data));
    }

    @NonNull
    public Observable<Cursor> removeAndRefresh(@NonNull T data) {
        return fromDatabaseTask(removeAndRefreshCall(data));
    }

    @NonNull
    public Observable<Void> remove(@NonNull T data) {
        return fromDatabaseTask(removeCall(data));
    }

    @NonNull
    public Observable<Cursor> removeAndRefresh(long id) {
        return fromDatabaseTask(removeAndRefreshCall(id));
    }

    @NonNull
    public Observable<Cursor> clear() {
        return fromDatabaseTask(removeAllRefresh());
    }

    @NonNull
    public Callable<Cursor> refresh() {
        return query(lastQuery());
    }

    @NonNull
    public <R> Observable<R> fromDatabaseTask(@NonNull Callable<R> task) {
        return Observable.fromCallable(callInTx(task))
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    public abstract T performGetById(long id);

    @NonNull
    public DaoSession session() {
        return session.get();
    }

    @NonNull
    protected abstract T performInsert(@NonNull T data);

    protected abstract void performRemove(@NonNull T data);

    protected abstract void performRemoveAll();

    @NonNull
    protected abstract CursorQuery allSortedByName();

    @NonNull
    protected abstract CursorQuery filteredSortedByNameQuery();

    protected abstract long readKey(@NonNull Cursor cursor, int columnIndex);

    @NonNull
    protected abstract Property getKeyProperty();

    protected abstract long getKey(T t);

    @NonNull
    private <R> Callable<R> callInTx(@NonNull final Callable<R> task) {
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                return session().callInTx(task);
            }
        };
    }

    @NonNull
    private Callable<T> fromId(final long id) {
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                return performGetById(id);
            }
        };
    }

    @NonNull
    private Callable<Cursor> filtered(@NonNull final String partOfName) {
        return query(getQueryOf(partOfName));
    }

    @NonNull
    private Callable<Cursor> addNewAndRefreshCall(@NonNull final T data) {
        return new Callable<Cursor>() {
            @Override
            public Cursor call() throws Exception {
                performInsert(data);
                return refresh().call();
            }
        };
    }

    private int getKeyColumn(@NonNull Cursor cursor) {
        return cursor.getColumnIndexOrThrow(getKeyProperty().columnName);
    }

    @NonNull
    private Callable<Cursor> removeAndRefreshCall(@NonNull final T data) {
        return new Callable<Cursor>() {
            @Override
            public Cursor call() throws Exception {
                performRemove(data);
                return refresh().call();
            }
        };
    }

    @NonNull
    private Callable<Void> removeCall(@NonNull final T data) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                performRemove(data);
                return null;
            }
        };
    }

    @NonNull
    private Callable<Cursor> removeAndRefreshCall(final long id) {
        return new Callable<Cursor>() {
            @Override
            public Cursor call() throws Exception {
                performRemove(id);
                return refresh().call();
            }
        };
    }

    private void performRemove(long id) {
        performRemove(performGetById(id));
    }

    @NonNull
    private Callable<Cursor> removeAllRefresh() {
        return new Callable<Cursor>() {
            @Override
            public Cursor call() throws Exception {
                performRemoveAll();
                return refresh().call();
            }
        };
    }

    @NonNull
    protected Callable<CursorQuery> lastQuery() {
        return getQueryOf(lastFilter);
    }

    @NonNull
    protected Callable<CursorQuery> getQueryOf(@NonNull final String partOfName) {
        return new Callable<CursorQuery>() {
            @Override
            public CursorQuery call() throws Exception {
                cacheLastQuery(partOfName);
                if (Strings.isNullOrEmpty(partOfName)) {
                    return allSortedByNameSingleton().forCurrentThread();
                } else {
                    CursorQuery query = filteredSortedByNameQuerySingleton().forCurrentThread();
                    query.setParameter(0, "%" + partOfName + "%");
                    return query;
                }
            }
        };
    }

    protected void cacheLastQuery(@NonNull final String partOfName) {
        lastFilter = partOfName;
    }

    @NonNull
    protected Callable<Cursor> query(@NonNull final Callable<CursorQuery> queryCall) {
        return new Callable<Cursor>() {
            @Override
            public Cursor call() throws Exception {
                CursorQuery query = queryCall.call();
                return query.query();
            }
        };
    }

    @NonNull
    protected CursorQuery allSortedByNameSingleton() {
        if (allSortedByNameQuery == null) {
            allSortedByNameQuery = allSortedByName();
        }
        return allSortedByNameQuery;
    }

    @NonNull
    protected CursorQuery filteredSortedByNameQuerySingleton() {
        if (filteredSortedByNameQuery == null) {
            filteredSortedByNameQuery = filteredSortedByNameQuery();
        }
        return filteredSortedByNameQuery;
    }

}
