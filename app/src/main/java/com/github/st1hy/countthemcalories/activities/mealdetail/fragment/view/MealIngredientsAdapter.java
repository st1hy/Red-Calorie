package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter.MealIngredientsPresenter;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.viewholder.IngredientViewHolder;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;

import javax.inject.Inject;


@PerFragment
public class MealIngredientsAdapter extends RecyclerView.Adapter<IngredientViewHolder> {

    @NonNull
    private final MealIngredientsPresenter presenter;

    @Inject
    public MealIngredientsAdapter(@NonNull MealIngredientsPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return presenter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        presenter.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }
}
