package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import com.github.st1hy.countthemcalories.activities.tags.fragment.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.core.adapter.ForwardingAdapter;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.core.tokensearch.TokenSearchView;
import com.github.st1hy.countthemcalories.database.TagDao;
import com.google.common.base.Optional;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

@PerActivity
public class SearchSuggestionsAdapter extends ForwardingAdapter<CursorAdapter> implements BasicLifecycle {

    private static final String COLUMN = TagDao.Properties.Name.columnName;

    @NonNull
    private final RxTagsDatabaseModel databaseModel;
    @NonNull
    private final TokenSearchView view;
    @NonNull
    private final Observable<SearchResult> searchResultObservable;
    private Optional<String> filter;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public SearchSuggestionsAdapter(@NonNull @Named("activityContext") Context context,
                                    @NonNull RxTagsDatabaseModel databaseModel,
                                    @NonNull TokenSearchView view,
                                    @NonNull Observable<SearchResult> searchResultObservable,
                                    @NonNull Optional<String> filter) {
        super(new SimpleCursorAdapter(context,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{COLUMN},
                new int[]{android.R.id.text1},
                0));
        this.databaseModel = databaseModel;
        this.view = view;
        this.searchResultObservable = searchResultObservable;
        this.filter = filter;
    }

    @Override
    public void onStart() {
        subscriptions.add(makeSuggestions(searchResultObservable));
        if (filter.isPresent()) {
            view.setQuery("", Collections.singletonList(filter.get()));
            view.expand(false);
            filter = Optional.absent();
        }
    }

    @Override
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
