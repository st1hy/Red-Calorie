package com.github.st1hy.countthemcalories.activities.addmeal.fragment.view;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.ingredientitems.IngredientItemViewHolder;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.IngredientsListPresenter;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerViewAdapter;

import javax.inject.Inject;

public class IngredientsListAdapter extends RecyclerViewAdapter<IngredientItemViewHolder> {

    @NonNull
    private final IngredientsListPresenter presenter;

    @Inject
    public IngredientsListAdapter(@NonNull IngredientsListPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onViewAttachedToWindow(IngredientItemViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.onAttached();
    }

    @Override
    public void onViewDetachedFromWindow(IngredientItemViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.onDetached();
    }

    @Override
    public void onBindViewHolder(IngredientItemViewHolder holder, int position) {
        presenter.onBindViewHolder(holder, position);
    }

    @Override
    public IngredientItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return presenter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return presenter.getItemViewType(position);
    }
}
