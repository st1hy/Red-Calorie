package com.github.st1hy.countthemcalories.activities.ingredients.inject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsFragment;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.SearchSuggestionsAdapter;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsScreen;
import com.github.st1hy.countthemcalories.activities.ingredients.view.SearchSuggestionsView;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.core.drawer.model.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.drawer.presenter.DrawerPresenter;
import com.github.st1hy.countthemcalories.core.drawer.presenter.DrawerPresenterImpl;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.github.st1hy.countthemcalories.core.tokensearch.RxSearchable;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;
import com.google.common.base.Optional;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity.ACTION_SELECT_INGREDIENT;

@Module
public class IngredientsActivityModule {

    public static int debounceTime = 250;

    private final IngredientsActivity activity;

    public IngredientsActivityModule(@NonNull IngredientsActivity activity) {
        this.activity = activity;
    }

    @Provides
    public IngredientsScreen provideView() {
        return activity;
    }

    @PerActivity
    @Provides
    public DrawerPresenter provideDrawerPresenter() {
        return new DrawerPresenterImpl(activity, DrawerMenuItem.INGREDIENTS);
    }

    @Provides
    @PerActivity
    public SearchSuggestionsAdapter provideSuggestionsAdapter(RxTagsDatabaseModel databaseModel,
                                                              SearchSuggestionsView view,
                                                              Optional<String> filter) {
        return new SearchSuggestionsAdapter(activity, databaseModel, view, filter);
    }

    @Provides
    public SearchSuggestionsView provideSearchView() {
        return activity;
    }

    @Provides
    public Optional<String> provideSearchFilter(Intent intent) {
        if (intent != null) {
            String tag = intent.getStringExtra(IngredientsActivity.EXTRA_TAG_FILTER_STRING);
            if (tag != null) {
                intent.removeExtra(IngredientsActivity.EXTRA_TAG_FILTER_STRING);
                return Optional.of(tag);
            }
        }
        return Optional.absent();
    }

    @Provides
    public IngredientsFragment provideContent(FragmentManager fragmentManager, Bundle arguments) {
        final String tag = "ingredients content";

        IngredientsFragment fragment = (IngredientsFragment) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new IngredientsFragment();
            fragment.setArguments(arguments);

            fragmentManager.beginTransaction()
                    .add(R.id.ingredients_content_frame, fragment, tag)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commit();
            fragmentManager.executePendingTransactions();
        }
        return fragment;
    }

    @Provides
    public FragmentManager provideFragmentManager() {
        return activity.getSupportFragmentManager();
    }

    @Provides
    public Bundle provideArguments(Intent intent) {
        Bundle arguments = new Bundle();
        boolean selectMode = intent != null &&
                ACTION_SELECT_INGREDIENT.equals(intent.getAction());
        arguments.putBoolean(IngredientsFragment.ARG_SELECT_BOOL, selectMode);
        return arguments;
    }

    @Provides
    public Intent provideIntent() {
        return activity.getIntent();
    }

    @Provides
    @PerActivity
    public Observable<SearchResult> provideSearchResults() {
        Observable<SearchResult> sequenceObservable = RxSearchable.create(activity.getSearchView())
                .subscribeOn(AndroidSchedulers.mainThread());
        if (debounceTime > 0) {
            sequenceObservable = sequenceObservable
                    .share()
                    // During restart tags are re-added in onRestoreSavedState which leads to
                    // 2 searches: immediate with wrong query and correct one after delay+250ms
                    // if delay is smaller than 100ms this will remove wrong search and speed it up
                    // to delay+100ms
                    .debounce(100, TimeUnit.MILLISECONDS).limit(1)
                    .concatWith(
                            sequenceObservable
                                    .debounce(debounceTime, TimeUnit.MILLISECONDS)
                    );
        }
        sequenceObservable = sequenceObservable
                .distinctUntilChanged()
                .replay(1)
                .autoConnect();
        return sequenceObservable;
    }

}
