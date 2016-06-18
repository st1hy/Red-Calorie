package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.github.st1hy.countthemcalories.activities.tags.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.core.adapter.ForwardingAdapter;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.database.TagDao;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class SearchSuggestionsAdapter extends ForwardingAdapter<CursorAdapter> {

    static final String COLUMN = TagDao.Properties.Name.columnName;

    final RxTagsDatabaseModel databaseModel;
    final CompositeSubscription subscriptions = new CompositeSubscription();

    public SearchSuggestionsAdapter(@NonNull Context context,
                                    @NonNull RxTagsDatabaseModel databaseModel) {
        super(new SimpleCursorAdapter(context,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{COLUMN},
                new int[]{android.R.id.text1},
                0));
        this.databaseModel = databaseModel;
    }

    public void onStart(@NonNull Observable<SearchResult> observable) {
        subscriptions.add(makeSuggestions(observable));

    }

    public void onStop() {
        subscriptions.clear();
        getParent().changeCursor(null);
    }

    /**
     * Some magic happens: while internally adapter uses cursor, from outside perspective this adapter
     * should be adapter of String, TokenSearchView assumes object it type of TokenSearchView
     * template parameter when creating chips
     */
    @Override
    public Object getItem(int position) {
        Cursor cursor = (Cursor) super.getItem(position);
        cursor.moveToPosition(position);
        return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN));
    }

    @NonNull
    private Subscription makeSuggestions(@NonNull final Observable<SearchResult> sequenceObservable) {
        return sequenceObservable
                .doOnNext(new Action1<SearchResult>() {
                    @Override
                    public void call(SearchResult searchResult) {
                        Timber.d("Tag search: %s", searchResult);
                    }
                })
                .flatMap(new Func1<SearchResult, Observable<Cursor>>() {
                    @Override
                    public Observable<Cursor> call(SearchResult searchResult) {
                        if (searchResult.getQuery().trim().length() > 0)
                            return databaseModel.getAllFiltered(searchResult.getQuery(), searchResult.getTokens());
                        else return Observable.just(null);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleSubscriber<Cursor>() {
                    @Override
                    public void onNext(Cursor cursor) {
                        getParent().changeCursor(cursor);
                    }
                });

    }


}
