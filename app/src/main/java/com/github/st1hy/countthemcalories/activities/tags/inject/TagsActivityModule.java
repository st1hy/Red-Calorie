package com.github.st1hy.countthemcalories.activities.tags.inject;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.fragment.TagsFragment;
import com.github.st1hy.countthemcalories.activities.tags.fragment.inject.TagsFragmentComponentFactory;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsScreen;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsScreenImpl;
import com.github.st1hy.countthemcalories.core.command.undo.inject.UndoModule;
import com.github.st1hy.countthemcalories.core.command.undo.inject.UndoRootView;
import com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.tokensearch.TokenSearchView;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerModule;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module(includes = {
        DrawerModule.class,
        UndoModule.class
})
public abstract class TagsActivityModule {

    @Binds
    public abstract TagsScreen tagsScreen(TagsScreenImpl screen);

    @Binds
    public abstract TagsFragmentComponentFactory tagsFragmentComponentFactory(TagsActivityComponent component);

    @Provides
    public static TagsFragment provideContent(FragmentManager fragmentManager,
                                              TagsFragmentComponentFactory componentFactory) {
        final String tag = "tags content";

        TagsFragment fragment = (TagsFragment) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new TagsFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.tags_content_frame, fragment, tag)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commitNow();
        }
        fragment.setComponentFactory(componentFactory);
        return fragment;
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
