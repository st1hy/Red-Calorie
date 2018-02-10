package com.github.st1hy.countthemcalories.database.rx;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.DaoSession;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.CursorQuery;

import java.util.concurrent.Callable;

import dagger.Lazy;
import rx.Observable;

public abstract class RxDatabaseModel<T> extends AbstractRxDatabaseModel {
    private CursorQuery allSortedByNameQuery;
    private CursorQuery filteredSortedByNameQuery;
    protected String lastFilter = "";

    public RxDatabaseModel(@NonNull Lazy<DaoSession> session) {
        super(session);
    }

    /**
     * @return position of the item in cursor or -1 if item could not be found inside the cursor
     */
    public int findInCursor(@NonNull Cursor cursor, T data) {
        long daoId = getKey(data);
        return findInCursor(cursor, daoId);
    }

    /**
     * @return position of the item in cursor or -1 if item could not be found inside the cursor
     */
    public int findInCursor(@NonNull Cursor cursor, long itemId) {
        int idColumn = cursor.getColumnIndexOrThrow(getKeyProperty().columnName);
        while (cursor.moveToNext()) {
            if (itemId == cursor.getLong(idColumn)) {
                return cursor.getPosition();
            }
        }
        return -1;
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
    public Observable<Void> remove(@NonNull T data) {
        return fromDatabaseTask(removeCall(data));
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
    public abstract T performGetById(long id);

    @NonNull
    protected abstract T performInsert(@NonNull T data);

    protected abstract void performRemove(@NonNull T data);

    protected abstract void performRemoveAll();

    @NonNull
    protected abstract CursorQuery allSortedByName();

    @NonNull
    protected abstract CursorQuery filteredSortedByNameQuery();

    @NonNull
    protected abstract Property getKeyProperty();

    protected abstract long getKey(@NonNull T t);


    @NonNull
    private Callable<T> fromId(final long id) {
        return () -> performGetById(id);
    }

    @NonNull
    private Callable<Cursor> filtered(@NonNull final String partOfName) {
        return query(getQueryOf(partOfName));
    }

    @NonNull
    private Callable<Void> removeCall(@NonNull final T data) {
        return () -> {
            performRemove(data);
            return null;
        };
    }

    @NonNull
    private Callable<Cursor> removeAllRefresh() {
        return () -> {
            performRemoveAll();
            return refresh().call();
        };
    }

    @NonNull
    protected Callable<CursorQuery> lastQuery() {
        return getQueryOf(lastFilter);
    }

    protected Callable<CursorQuery> getQueryOf(@NonNull final String partOfName) {
        return () -> {
            cacheLastQuery(partOfName);
            if (partOfName.isEmpty()) {
                return allSortedByNameSingleton().forCurrentThread();
            } else {
                CursorQuery query = filteredSortedByNameQuerySingleton().forCurrentThread();
                query.setParameter(0, "%" + partOfName + "%");
                return query;
            }
        };
    }

    protected void cacheLastQuery(@NonNull final String partOfName) {
        lastFilter = partOfName;
    }

    @NonNull
    protected Callable<Cursor> query(@NonNull final Callable<CursorQuery> queryCall) {
        return () -> {
            CursorQuery query = queryCall.call();
            return query.query();
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
    private CursorQuery filteredSortedByNameQuerySingleton() {
        if (filteredSortedByNameQuery == null) {
            filteredSortedByNameQuery = filteredSortedByNameQuery();
        }
        return filteredSortedByNameQuery;
    }

}
