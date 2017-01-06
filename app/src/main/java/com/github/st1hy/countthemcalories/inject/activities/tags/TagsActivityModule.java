package com.github.st1hy.countthemcalories.inject.activities.tags;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.TagsActivity;
import com.github.st1hy.countthemcalories.activities.tags.fragment.TagsFragment;
import com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem;
import com.github.st1hy.countthemcalories.core.tokensearch.TokenSearchView;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.tags.fragment.TagsFragmentComponentFactory;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(includes = TagsActivityBindings.class)
public class TagsActivityModule {
    private final TagsActivity activity;

    public TagsActivityModule(@NonNull TagsActivity activity) {
        this.activity = activity;
    }

    @Provides
    public static TagsFragment provideContent(Bundle arguments,
                                              FragmentManager fragmentManager,
                                              TagsFragmentComponentFactory componentFactory) {
        final String tag = "tags content";

        TagsFragment fragment = (TagsFragment) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new TagsFragment();
            fragment.setArguments(arguments);

            fragmentManager.beginTransaction()
                    .add(R.id.tags_content_frame, fragment, tag)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commitNow();
        }
        fragment.setComponentFactory(componentFactory);
        return fragment;
    }

    @Provides
    public static Bundle provideArguments(Boolean inSelectMode, String[] excludedTags) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(TagsFragment.ARG_PICK_BOOL, inSelectMode);
        bundle.putStringArray(TagsFragment.ARG_EXCLUDED_TAGS_STRING_ARRAY, excludedTags);
        return bundle;
    }

    @Provides
    public static Boolean provideIsInSelectMode(@Nullable Intent intent) {
        return intent != null && TagsActivity.ACTION_PICK_TAG.equals(intent.getAction());
    }

    @Provides
    public static String[] provideExcludedTags(@Nullable Intent intent) {
        String[] tags = null;
        if (intent != null) {
            tags = intent.getStringArrayExtra(TagsActivity.EXTRA_EXCLUDE_TAG_STRING_ARRAY);
        }
        if (tags == null) tags = new String[0];
        return tags;
    }

    @Provides
    @Nullable
    public static Intent provideIntent(Activity activity) {
        return activity.getIntent();
    }

    @Provides
    public static FragmentManager provideFragmentManager(AppCompatActivity activity) {
        return activity.getSupportFragmentManager();
    }

    @Provides
    @PerActivity
    @Named("undoViewRoot")
    public static View undoRootView(Activity activity) {
        return activity.findViewById(R.id.tags_root);
    }

    @Provides
    public AppCompatActivity appCompatActivity() {
        return activity;
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
