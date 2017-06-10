package com.github.st1hy.countthemcalories.activities.tags.inject;

import android.app.Activity;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.fragment.TagsFragment;
import com.github.st1hy.countthemcalories.activities.tags.fragment.inject.TagsFragmentComponentFactory;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsScreen;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsScreenImpl;
import com.github.st1hy.countthemcalories.core.command.undo.inject.UndoModule;
import com.github.st1hy.countthemcalories.core.command.undo.inject.UndoRootView;
import com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.drawer.DrawerModule;
import com.github.st1hy.countthemcalories.core.fragments.FragmentLocation;
import com.github.st1hy.countthemcalories.core.fragments.FragmentTransactionModule;
import com.github.st1hy.countthemcalories.core.tokensearch.TokenSearchView;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module(includes = {
        DrawerModule.class,
        UndoModule.class,
        FragmentTransactionModule.class,
})
public abstract class TagsActivityModule {

    private static final String TAGS_CONTENT_TAG = "tags content";

    @Binds
    public abstract TagsScreen tagsScreen(TagsScreenImpl screen);

    @Binds
    public abstract TagsFragmentComponentFactory tagsFragmentComponentFactory(TagsActivityComponent component);

    @Provides
    @IntoMap
    @StringKey(TAGS_CONTENT_TAG)
    @Reusable
    public static FragmentLocation tagsContent(TagsFragmentComponentFactory componentFactory) {
        return new FragmentLocation.Builder<>(TagsFragment.class, TAGS_CONTENT_TAG)
                .setViewRootId(R.id.tags_content_frame)
                .setInjector(fragment -> fragment.setComponentFactory(componentFactory))
                .build();
    }

    @Provides
    @PerActivity
    @UndoRootView
    public static View undoRootView(Activity activity) {
        return activity.findViewById(R.id.tags_root);
    }

    @Provides
    public static DrawerMenuItem drawerMenuItem() {
        return DrawerMenuItem.CATEGORIES;
    }

    @Provides
    @PerActivity
    public static TokenSearchView settingsView(Activity activity) {
        TokenSearchView searchView = (TokenSearchView) activity.findViewById(R.id.tags_search_view);
        searchView.getSearchTextView().setSplitChar(new char[]{0xAD});
        return searchView;
    }

}
