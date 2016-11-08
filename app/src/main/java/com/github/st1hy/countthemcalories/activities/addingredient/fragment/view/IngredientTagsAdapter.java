package com.github.st1hy.countthemcalories.activities.addingredient.fragment.view;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.IngredientTagsPresenter;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.viewholder.TagViewHolder;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerViewAdapter;

import javax.inject.Inject;

public class IngredientTagsAdapter extends RecyclerViewAdapter<TagViewHolder> {

    @NonNull private final IngredientTagsPresenter presenter;

    @Inject
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
    public int getItemCount() {
        return presenter.getItemCount();
    }
}
