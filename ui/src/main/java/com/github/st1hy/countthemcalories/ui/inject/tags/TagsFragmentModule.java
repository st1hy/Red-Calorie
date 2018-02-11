package com.github.st1hy.countthemcalories.ui.inject.tags;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.ui.activities.tags.adapter.TagsDaoAdapter;
import com.github.st1hy.countthemcalories.ui.activities.tags.adapter.holder.TagViewHolder;
import com.github.st1hy.countthemcalories.ui.activities.tags.model.ClickEvent;
import com.github.st1hy.countthemcalories.ui.activities.tags.model.Tags;
import com.github.st1hy.countthemcalories.ui.activities.tags.model.TagsFragmentModel;
import com.github.st1hy.countthemcalories.ui.activities.tags.presenter.TagsStateSaver;
import com.github.st1hy.countthemcalories.ui.activities.tags.view.TagsView;
import com.github.st1hy.countthemcalories.ui.activities.tags.view.TagsViewImpl;
import com.github.st1hy.countthemcalories.ui.contract.Tag;
import com.github.st1hy.countthemcalories.ui.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.bundle.FragmentSavedState;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.context.ActivityContext;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.FragmentRootView;
import com.google.common.collect.Sets;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Provider;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.subjects.PublishSubject;

@Module
public abstract class TagsFragmentModule {

    @Binds
    public abstract TagsView provideView(TagsViewImpl view);

    @Binds
    public abstract TagComponentFactory tagComponent(TagsFragmentComponent component);

    @Provides
    @PerFragment
    public static RecyclerView recyclerView(@FragmentRootView View rootView,
                                            @ActivityContext Context context,
                                            TagsDaoAdapter adapter) {
        RecyclerView recyclerView = rootView.findViewById(R.id.tags_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    @Provides
    @PerFragment
    public static TagsFragmentModel fragmentModel(@Nullable @FragmentSavedState Bundle savedState,
                                                  @Named("isInSelectMode") Boolean isInSelectMode,
                                                  Provider<Set<Tag>> tagsProvider) {
        if (savedState != null) {
            return Parcels.unwrap(savedState.getParcelable(TagsStateSaver.MODEL));
        } else {
            return new TagsFragmentModel(isInSelectMode, tagsProvider.get());
        }
    }

    @Provides
    @PerFragment
    public static PublishSubject<TagViewHolder> stateChanges() {
        return PublishSubject.create();
    }

    @Provides
    @PerFragment
    public static PublishSubject<ClickEvent> clickEvents() {
        return PublishSubject.create();
    }

    @Provides
    @PerFragment
    public static PublishSubject<RecyclerEvent> eventSubject() {
        return PublishSubject.create();
    }

    @Provides
    @PerFragment
    public static Observable<RecyclerEvent> recyclerEventObservable(PublishSubject<RecyclerEvent> events) {
        return events.asObservable();
    }

    @Provides
    public static Tags provideSelectedTags(@Nullable Intent intent) {
        Tags tags = null;
        if (intent != null) {
            tags = Parcels.unwrap(intent.getParcelableExtra(TagsActivity.extraSelectedTags));
        }
        if (tags == null) tags = new Tags(Collections.emptyList());
        return tags;
    }

    @Provides
    public static Set<Tag> selectedTags(Provider<Tags> tagsProvider) {
        return Sets.newHashSet(tagsProvider.get().getTags());
    }

    @Provides
    @Named("isInSelectMode")
    public static Boolean provideIsInSelectMode(@Nullable Intent intent) {
        return intent != null && TagsActivity.actionPickTag.equals(intent.getAction());
    }

}
