package com.github.st1hy.countthemcalories.activities.overview.fragment.view;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems.AbstractMealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.MealsPresenter;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerViewAdapter;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;

@PerFragment
public class MealsRecyclerViewAdapter extends RecyclerViewAdapter<AbstractMealItemHolder> {

    @NonNull
    private final MealsPresenter presenter;

    @Inject
    public MealsRecyclerViewAdapter(@NonNull MealsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onBindViewHolder(AbstractMealItemHolder holder, int position) {
        presenter.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return presenter.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }

    @Override
    public AbstractMealItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return presenter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onViewDetachedFromWindow(AbstractMealItemHolder holder) {
        presenter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewAttachedToWindow(AbstractMealItemHolder holder) {
        presenter.onViewAttachedToWindow(holder);
    }
}
