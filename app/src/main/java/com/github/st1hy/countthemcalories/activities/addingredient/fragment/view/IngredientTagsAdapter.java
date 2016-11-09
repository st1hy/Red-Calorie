package com.github.st1hy.countthemcalories.activities.addingredient.fragment.view;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.IngredientTagsPresenter;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.viewholder.TagViewHolder;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerViewAdapter;

public class IngredientTagsAdapter extends RecyclerViewAdapter<TagViewHolder> {

    @NonNull private final IngredientTagsPresenter presenter;

    public IngredientTagsAdapter(@NonNull IngredientTagsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return presenter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        presenter.onBindViewHolder(holder, position);
    }

    @Override
    public void onViewAttachedToWindow(TagViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(TagViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }
}
