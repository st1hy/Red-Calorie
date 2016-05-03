package com.github.st1hy.countthemcalories.core.rx;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.event.DbProcessing;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.google.common.base.Strings;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import de.greenrobot.dao.query.Query;
import rx.Observable;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public abstract class RxDatabaseModel<T> {

    protected final DaoSession session;
    protected List<T> list = Collections.emptyList();
    protected final ObservableValue<DbProcessing> dbProcessingValue = new ObservableValue<>(DbProcessing.NOT_STARTED);
    private String partOfLastQuery = "";
    private Query<T> allSortedByNameQuery;
    private Query<T> filteredSortedByNameQuery;
    private Query<T> lastQuery;

    public RxDatabaseModel(@NonNull DaoSession session) {
        this.session = session;
    }

    @NonNull
    public Observable<DbProcessing> getDbProcessingObservable() {
        return dbProcessingValue.asObservable();
    }

    @NonNull
    public T getItemAt(int position) {
        return list.get(position);
    }

    public int getItemCount() {
        return list.size();
    }

    @NonNull
    public Observable<T> getById(long id) {
        return fromDatabaseTask(fromId(id));
    }

    @NonNull
    public Observable<List<T>> getAllObservable() {
        return fromDatabaseTask(loadAll());
    }

    @NonNull
    public Observable<List<T>> getAllFiltered(@NonNull String partOfName) {
        if (partOfName.equals(partOfLastQuery)) {
            return Observable.just(list);
        } else {
            partOfLastQuery = partOfName;
            return fromDatabaseTask(filtered(partOfName));
        }
    }

    /**
     * @return observable position of the added tag on the list
     */
    @NonNull
    public Observable<Integer> addNewAndRefresh(@NonNull T data) {
        return fromDatabaseTask(addNewAndRefreshCall(data));
    }

    @NonNull
    public Observable<List<T>> removeAndRefresh(@NonNull T data) {
        return fromDatabaseTask(removeAndRefreshCall(data));
    }

    @NonNull
    public Observable<List<T>> clear() {
        return fromDatabaseTask(removeAllRefresh());
    }

    protected abstract T performGetById(long id);

    @NonNull
    protected abstract List<T> performQuery(@NonNull Query<T> query);

    @NonNull
    protected abstract T performInsert(@NonNull T data);

    protected abstract void performRemove(@NonNull T data);

    protected abstract void performRemoveAll();

    @NonNull
    protected abstract Query<T> allSortedByName();

    @NonNull
    protected abstract Query<T> filteredSortedByNameQuery();

    protected abstract boolean equal(@NonNull T a, @NonNull T b);

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
                return session.callInTx(task);
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

    @NonNull
    private Callable<List<T>> loadAll() {
        return filtered("");
    }

    @NonNull
    private Callable<List<T>> filtered(@NonNull final String partOfName) {
        return query(getQueryOf(partOfName));
    }

    @NonNull
    private Callable<Integer> addNewAndRefreshCall(@NonNull final T data) {
        return new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                T newItem = performInsert(data);
                refresh().call();
                for (int i = 0; i < list.size(); i++) {
                    if (equal(newItem, getItemAt(i))) return i;
                }
                throw new IllegalStateException("Could not found newly added tag on the list!");
            }
        };
    }

    @NonNull
    private Callable<List<T>> removeAndRefreshCall(@NonNull final T data) {
        return new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                performRemove(data);
                return refresh().call();
            }
        };
    }

    @NonNull
    private Callable<List<T>> removeAllRefresh() {
        return new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                performRemoveAll();
                return refresh().call();
            }
        };
    }

    @NonNull
    protected Callable<List<T>> refresh() {
        return query(lastQuery());
    }

    @NonNull
    private Callable<Query<T>> lastQuery() {
        return new Callable<Query<T>>() {
            @Override
            public Query<T> call() throws Exception {
                if (lastQuery == null) {
                    return getQueryOf("").call();
                } else {
                    return lastQuery;
                }
            }
        };
    }

    @NonNull
    private Callable<Query<T>> getQueryOf(@NonNull final String partOfName) {
        return new Callable<Query<T>>() {
            @Override
            public Query<T> call() throws Exception {
                if (Strings.isNullOrEmpty(partOfName)) {
                    return allSortedByNameSingleton().forCurrentThread();
                } else {
                    Query<T> query = filteredSortedByNameQuerySingleton().forCurrentThread();
                    query.setParameter(0, "%" + partOfName + "%");
                    return query;
                }
            }
        };
    }
    @NonNull
    private Callable<List<T>> query(@NonNull final Callable<Query<T>> queryCall) {
        return new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                Query<T> query = queryCall.call();
                lastQuery = query;
                List<T> list = performQuery(query);
                RxDatabaseModel.this.list = list;
                return list;
            }
        };
    }

    @NonNull
    protected Query<T> allSortedByNameSingleton() {
        if (allSortedByNameQuery == null) {
            allSortedByNameQuery = allSortedByName();
        }
        return allSortedByNameQuery;
    }

    @NonNull
    protected Query<T> filteredSortedByNameQuerySingleton() {
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
