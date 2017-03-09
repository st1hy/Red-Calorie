package com.github.st1hy.countthemcalories.inject.activities.tags.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.fragment.TagsFragment;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.Tags;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.TagsFragmentModel;
import com.github.st1hy.countthemcalories.activities.tags.fragment.presenter.TagsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.tags.fragment.presenter.TagsStateSaver;
import com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder.TagViewHolder;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.google.common.collect.Sets;

import org.parceler.Parcels;

import java.util.Set;

import javax.inject.Named;
import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.subjects.PublishSubject;

@Module(includes = TagsFragmentBindings.class)
public class TagsFragmentModule {

    private final TagsFragment fragment;
    @Nullable
    private final Bundle savedState;

    public TagsFragmentModule(@NonNull TagsFragment fragment,
                              @Nullable Bundle savedState) {
        this.fragment = fragment;
        this.savedState = savedState;
    }

    @Provides
    @Named("fragmentRootView")
    public View rootView() {
        return fragment.getView();
    }

    @Provides
    @PerFragment
    public static RecyclerView recyclerView(@Named("fragmentRootView") View rootView,
                                            @Named("activityContext") Context context,
                                            TagsDaoAdapter adapter) {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.tags_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    @Provides
    @Nullable
    public Bundle savedState() {
        return savedState;
    }

    @Provides
    public static Set<Tag> selectedTags(Provider<Tags> tagsProvider) {
        return Sets.newHashSet(tagsProvider.get().getTags());
    }

    @Provides
    @PerFragment
    public static TagsFragmentModel fragmentModel(@Nullable Bundle savedState,
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
    public static PublishSubject<RecyclerEvent> eventSubject() {
        return PublishSubject.create();
    }

    @Provides
    @PerFragment
    public static Observable<RecyclerEvent> recyclerEventObservable(PublishSubject<RecyclerEvent> events) {
        return events.asObservable();
    }

}
