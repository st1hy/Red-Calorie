package com.github.st1hy.countthemcalories.core.rx;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.event.DbProcessing;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import dagger.Lazy;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.CursorQuery;
import rx.Observable;
import rx.functions.Action0;
import timber.log.Timber;

public abstract class RxDatabaseModel<T> {
    private final Lazy<DaoSession> session;
    private final ObservableValue<DbProcessing> dbProcessingValue = new ObservableValue<>(DbProcessing.NOT_STARTED);
    private CursorQuery allSortedByNameQuery;
    private CursorQuery filteredSortedByNameQuery;
    private String lastFilter = "";

    public RxDatabaseModel(@NonNull Lazy<DaoSession> session) {
        this.session = session;
    }


    /**
     * Reads tag entity which is NOT cached or attached to dao into output
     */
    public abstract void performReadEntity(@NonNull Cursor cursor, @NonNull T output);

    @NonNull
    public Observable<DbProcessing> getDbProcessingObservable() {
        return dbProcessingValue.asObservable();
    }

    @NonNull
    public Observable<T> getById(long id) {
        return fromDatabaseTask(fromId(id));
    }

    @NonNull
    public Observable<List<T>> getById(long id, long... ids) {
        return fromDatabaseTask(fromId(id, ids));
    }

    @NonNull
    public Observable<T> getFromCursor(@NonNull Cursor cursor, int position) {
        return fromDatabaseTask(fromCursor(cursor, position));
    }

    @NonNull
    public Observable<Cursor> getAllObservable() {
        return fromDatabaseTask(loadAll());
    }

    @NonNull
    public Observable<Cursor> getAllFiltered(@NonNull String partOfName) {
        return fromDatabaseTask(filtered(partOfName));
    }

    /**
     * @return observable with Cursor at position of the added tag on the list, if its not filtered out
     * in that case cursor will be moved to first element
     */
    @NonNull
    public Observable<Cursor> addNewAndRefresh(@NonNull T data) {
        return fromDatabaseTask(addNewAndRefreshCall(data));
    }

    @NonNull
    public Observable<Cursor> removeAndRefresh(@NonNull T data) {
        return fromDatabaseTask(removeAndRefreshCall(data));
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
    protected abstract T performGetById(long id);

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
    protected DaoSession session() {
        return session.get();
    }

    @NonNull
    protected <R> Observable<R> fromDatabaseTask(@NonNull Callable<R> task) {
        return Observable.fromCallable(callInTx(task))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(onStarted())
                .doOnTerminate(onFinished());
    }

    @NonNull
    private <R> Callable<R> callInTx(@NonNull final Callable<R> task) {
        return new Callable<R>() {
            @Override
            public R call() throws Exception {
                return session().callInTx(task);
            }
        };
    }

    private Callable<T> fromCursor(@NonNull final Cursor cursor, final int position) {
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                cursor.moveToPosition(position);
                long id = readKey(cursor, getKeyColumn(cursor));
                return performGetById(id);
            }
        };
    }

    private Callable<T> fromId(final long id) {
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                return performGetById(id);
            }
        };
    }

    private Callable<List<T>> fromId(final long id, final long... ids) {
        return new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                List<T> list = new ArrayList<>(ids.length + 1);
                list.add(performGetById(id));
                for (long i : ids) {
                    list.add(performGetById(i));
                }
                return list;
            }
        };
    }

    @NonNull
    private Callable<Cursor> loadAll() {
        return filtered("");
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
                T newItem = performInsert(data);
                Cursor cursor = refresh().call();
                Timber.d("Cursor: %s", cursor);

                final long newKey = getKey(newItem);
                moveCursorToKeyIfAble(cursor, newKey);
                return cursor;
            }
        };
    }

    private void moveCursorToKeyIfAble(@NonNull Cursor cursor, long key) {
        if (cursor.moveToFirst()) {
            int keyColumn = getKeyColumn(cursor);
            boolean isFound = false;
            do {
                long readKey = readKey(cursor, keyColumn);
                if (readKey == key) {
                    isFound = true;
                    break;
                }
            } while (cursor.moveToNext());
            if (!isFound) cursor.moveToFirst();
        }
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
    protected Callable<Cursor> refresh() {
        return query(lastQuery());
    }

    @NonNull
    private Callable<CursorQuery> lastQuery() {
        return getQueryOf(lastFilter);
    }

    @NonNull
    private Callable<CursorQuery> getQueryOf(@NonNull final String partOfName) {
        return new Callable<CursorQuery>() {
            @Override
            public CursorQuery call() throws Exception {
                lastFilter = partOfName;
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

    @NonNull
    private Callable<Cursor> query(@NonNull final Callable<CursorQuery> queryCall) {
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

    @NonNull
    private Action0 onFinished() {
        return new Action0() {
            @Override
            public void call() {
                dbProcessingValue.setValue(DbProcessing.FINISHED);
            }
        };
    }

    @NonNull
    private Action0 onStarted() {
        return new Action0() {
            @Override
            public void call() {
                dbProcessingValue.setValue(DbProcessing.STARTED);
            }
        };
    }

}
