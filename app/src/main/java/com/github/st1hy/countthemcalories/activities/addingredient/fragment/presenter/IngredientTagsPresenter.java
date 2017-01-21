package com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.IngredientTagsModel;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.view.AddIngredientView;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.viewholder.ItemTagViewHolder;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.viewholder.TagViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.Tags;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

@PerFragment
public class IngredientTagsPresenter extends RecyclerAdapterWrapper<TagViewHolder> implements OnItemClicked<Tag>, BasicLifecycle {

    private static final int TAG = R.layout.add_ingredient_tag;

    @NonNull
    private final IngredientTagsModel model;
    @NonNull
    private final AddIngredientView view;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public IngredientTagsPresenter(@NonNull IngredientTagsModel model, @NonNull AddIngredientView view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void onStart() {
        subscriptions.add(
                view.addTagObservable()
                        .map(aVoid -> new Tags(model.copyTags()))
                        .compose(view.selectTags())
                        .subscribe(tags -> {
                            model.replaceWith(tags);
                            notifyDataSetChanged();
                        })
        );
        subscriptions.add(
                model.getTagsSizeObservable()
                        .map(size -> Visibility.of(size == 0))
                        .subscribe(view::setNoCategoriesVisibility)
        );
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        switch (viewType) {
            case TAG:
                return new ItemTagViewHolder(view, this);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TAG:
                onBindTag((ItemTagViewHolder) holder, position);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private void onBindTag(@NonNull ItemTagViewHolder viewHolder, int position) {
        Tag tag = model.getTagAt(position);
        viewHolder.setCategoryName(tag.getName());
        viewHolder.setTag(tag);
    }

    @Override
    public int getItemCount() {
        return model.getSize();
    }

    public int getItemViewType(int position) {
        if (position < model.getSize()) {
            return TAG;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void onItemClicked(@NonNull Tag tag) {
        int position = model.remove(tag);
        notifyItemRemoved(position);
    }

}
