package com.github.st1hy.countthemcalories.inject.activities.ingredients;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.IngredientsFragment;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.SearchSuggestionsAdapter;
import com.github.st1hy.countthemcalories.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsScreen;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsScreenImpl;
import com.github.st1hy.countthemcalories.core.command.undo.UndoView;
import com.github.st1hy.countthemcalories.core.command.undo.UndoViewImpl;
import com.github.st1hy.countthemcalories.core.dialog.DialogView;
import com.github.st1hy.countthemcalories.core.dialog.DialogViewController;
import com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.core.tokensearch.RxSearchable;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.core.tokensearch.TokenSearchTextView;
import com.github.st1hy.countthemcalories.core.tokensearch.TokenSearchView;
import com.google.common.base.Optional;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import butterknife.ButterKnife;
import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.github.st1hy.countthemcalories.activities.ingredients.IngredientsActivity.ACTION_SELECT_INGREDIENT;

@Module
public class IngredientsActivityModule {

    public static int debounceTime = 250;

    private final IngredientsActivity activity;

    public IngredientsActivityModule(@NonNull IngredientsActivity activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);
    }

    @Provides
    @PerActivity
    public IngredientsScreen provideView(IngredientsScreenImpl ingredientsScreen) {
        return ingredientsScreen;
    }

    @Provides
    public Activity activity() {
        return activity;
    }

    @Provides
    public AppCompatActivity appCompatActivity() {
        return activity;
    }

    @Provides
    public DrawerMenuItem currentItem() {
        return DrawerMenuItem.INGREDIENTS;
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
    public FragmentManager provideFragmentManager(AppCompatActivity activity) {
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
    public Intent provideIntent(Activity activity) {
        return activity.getIntent();
    }


    @Provides
    @PerActivity
    public TokenSearchTextView tokenSearchTextView(Activity activity) {
        return (TokenSearchTextView) activity.findViewById(R.id.ingredients_search_view);
    }

    @Provides
    @PerActivity
    public TokenSearchView tokenSearchView(Activity activity) {
        return (TokenSearchView) activity.findViewById(R.id.ingredients_search_view);
    }

    @Provides
    @Named("suggestions")
    public SearchSuggestionsAdapter suggestionsAdapter(SearchSuggestionsAdapter adapter, TokenSearchTextView searchView) {
        searchView.setAdapter(adapter);
        return adapter;
    }

    @Provides
    @PerActivity
    public Observable<SearchResult> provideSearchResults(TokenSearchTextView tokenSearchTextView) {
        Observable<SearchResult> sequenceObservable = RxSearchable.create(tokenSearchTextView)
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

    @Provides
    @Named("undoViewRoot")
    public View undoRootView(Activity activity) {
        return activity.findViewById(R.id.ingredients_root);
    }

    @Provides
    @PerActivity
    public UndoView undoView(@Named("undoViewRoot") View rootView) {
        return new UndoViewImpl(rootView);
    }

    @Provides
    @PerActivity
    public DialogView dialogView(DialogViewController dialogViewController) {
        return dialogViewController;
    }

    @Provides
    @Named("activityContext")
    public Context context() {
        return activity;
    }
}
