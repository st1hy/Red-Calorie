package com.github.st1hy.countthemcalories.core.adapter;

import android.database.Cursor;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public abstract class RxDaoSearchAdapter<T extends RecyclerView.ViewHolder> extends CursorRecyclerViewAdapter<T> {

    private final static int DEBOUNCE_TIME_MS = 250;
    private final SearchableDatabase db;

    protected String lastQuery = "";

    public RxDaoSearchAdapter(@NonNull SearchableDatabase db) {
        this.db = db;
    }

    @CheckResult
    @NonNull
    public final Observable.Transformer<CharSequence, QueryResult> searchDatabase() {
        return charSequenceObservable -> {
            Observable<CharSequence> sequenceObservable = charSequenceObservable
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .share();
            sequenceObservable = sequenceObservable
                    .limit(1)
                    .concatWith(
                            sequenceObservable
                                    .skip(1)
                                    .debounce(DEBOUNCE_TIME_MS, TimeUnit.MILLISECONDS)
                    );
            return sequenceObservable
                    .map(CharSequence::toString)
                    .flatMap(query -> getAllWithFilter(query)
                            .map(cursor -> QueryResult.of(query, cursor)))
                    .retry(1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(result -> {
                        lastQuery = result.getQuery();
                        onCursorUpdate(result);
                        notifyDataSetChanged();
                    });
        };
    }

    @CallSuper
    protected void onCursorUpdate(@NonNull QueryResult queryResult) {
        super.onCursorUpdate(queryResult.getCursor());
    }

    @NonNull
    @CheckResult
    private Observable<Cursor> getAllWithFilter(@NonNull String filter) {
        return db.getAllFiltered(filter);
    }

    public static class QueryResult {
        private final Cursor cursor;
        private final String query;

        private QueryResult(@NonNull String query, @NonNull Cursor cursor) {
            this.cursor = cursor;
            this.query = query;
        }

        public static QueryResult of(@NonNull String query, @NonNull Cursor cursor) {
            return new QueryResult(query, cursor);
        }

        @NonNull
        public Cursor getCursor() {
            return cursor;
        }

        @NonNull
        public String getQuery() {
            return query;
        }
    }
}
