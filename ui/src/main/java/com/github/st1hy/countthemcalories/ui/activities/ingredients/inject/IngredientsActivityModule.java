package com.github.st1hy.countthemcalories.ui.activities.ingredients.inject;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.IngredientsFragment;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.inject.IngredientsFragmentComponentFactory;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.presenter.SearchSuggestionsAdapter;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.view.IngredientsScreen;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.view.IngredientsScreenImpl;
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityLauncherModule;
import com.github.st1hy.countthemcalories.ui.core.command.undo.inject.UndoModule;
import com.github.st1hy.countthemcalories.ui.core.command.undo.inject.UndoRootView;
import com.github.st1hy.countthemcalories.ui.core.drawer.DrawerMenuItem;
import com.github.st1hy.countthemcalories.ui.core.FragmentLocation;
import com.github.st1hy.countthemcalories.ui.inject.core.PermissionModule;
import com.github.st1hy.countthemcalories.ui.core.tokensearch.RxSearchable;
import com.github.st1hy.countthemcalories.ui.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.ui.core.tokensearch.TokenSearchTextView;
import com.github.st1hy.countthemcalories.ui.core.tokensearch.TokenSearchView;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.google.common.base.Optional;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

@Module(includes = {
        UndoModule.class,
        ActivityLauncherModule.class,
        PermissionModule.class,
})
@SuppressWarnings("Guava")
public abstract class IngredientsActivityModule {

    private final static int DEBOUNCE_TIME_MS = 250;

    private static final String INGREDIENTS_CONTENT = "ingredients content";

    @Binds
    public abstract IngredientsScreen provideView(IngredientsScreenImpl ingredientsScreen);

    @Binds
    public abstract IngredientsFragmentComponentFactory childComponentFactory(
            IngredientsActivityComponent component);

    @Provides
    static DrawerMenuItem currentItem() {
        return DrawerMenuItem.INGREDIENTS;
    }

    @Provides
    static Optional<String> provideSearchFilter(Intent intent) {
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
    @IntoMap
    @StringKey(INGREDIENTS_CONTENT)
    @Reusable
    static FragmentLocation ingredientsContent(IngredientsFragmentComponentFactory component) {
        return new FragmentLocation.Builder<>(IngredientsFragment.class, INGREDIENTS_CONTENT)
                .setViewRootId(R.id.ingredients_content_frame)
                .setInjector(fragment -> fragment.setComponentFactory(component))
                .build();
    }

    @Provides
    @PerActivity
    static TokenSearchTextView tokenSearchTextView(TokenSearchView view) {
        return (TokenSearchTextView) view.findViewById(R.id.token_search_text_view);
    }

    @Provides
    @PerActivity
    static TokenSearchView tokenSearchView(Activity activity) {
        return (TokenSearchView) activity.findViewById(R.id.ingredients_search_view);
    }

    @PerActivity
    @Provides
    @Named("touchOverlay")
    static View touchOverlay(Activity activity) {
        return activity.findViewById(R.id.ingredients_touch_overlay);
    }

    @Provides
    @Named("suggestions")
    static SearchSuggestionsAdapter suggestionsAdapter(SearchSuggestionsAdapter adapter,
                                                              TokenSearchTextView searchView) {
        searchView.setAdapter(adapter);
        return adapter;
    }

    @Provides
    @PerActivity
    static Observable<SearchResult> provideSearchResults(TokenSearchTextView tokenSearchTextView) {
        Observable<SearchResult> sequenceObservable = RxSearchable.create(tokenSearchTextView)
                .subscribeOn(AndroidSchedulers.mainThread());
        sequenceObservable = sequenceObservable
                .share()
                // During restart tags are re-added in onRestoreSavedState which leads to
                // 2 searches: immediate with wrong query and correct one after delay+250ms
                // if delay is smaller than 100ms this will remove wrong search and speed it up
                // to delay+100ms
                .debounce(100, TimeUnit.MILLISECONDS).limit(1)
                .concatWith(
                        sequenceObservable
                                .debounce(DEBOUNCE_TIME_MS, TimeUnit.MILLISECONDS)
                );
        sequenceObservable = sequenceObservable
                .distinctUntilChanged()
                .replay(1)
                .autoConnect();
        return sequenceObservable;
    }

    @Provides
    @UndoRootView
    static View undoRootView(Activity activity) {
        return activity.findViewById(R.id.ingredients_root);
    }

}
