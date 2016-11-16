package com.github.st1hy.countthemcalories.activities.overview.fragment.presenter;

import com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems.AbstractMealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems.OnMealInteraction;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerAdapterWrapper;

public interface MealsPresenter extends RecyclerAdapterWrapper<AbstractMealItemHolder>,
        OnMealInteraction, BasicLifecycle {

    int getItemViewType(int position);

    void onViewAttachedToWindow(AbstractMealItemHolder holder);

    void onViewDetachedFromWindow(AbstractMealItemHolder holder);
}
