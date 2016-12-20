package com.github.st1hy.countthemcalories.inject.activities.overview.fragment.mealitems;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems.AbstractMealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems.EmptyMealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems.MealItemHolder;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class MealRowModule {

    @LayoutRes
    private final int layoutRes;
    @NonNull
    private final ViewGroup parent;

    public MealRowModule(@LayoutRes int layoutRes,
                         @NonNull ViewGroup parent) {
        this.parent = parent;
        this.layoutRes = layoutRes;
    }

    @Provides
    @Named("mealItemRoot")
    public View rootView() {
        return LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
    }

    @Provides
    @Named("mealRow")
    public AbstractMealItemHolder mealItemHolder(MealItemHolder holder) {
        holder.fillParent(parent);
        return holder;
    }

    @Provides
    @Named("emptySpace")
    public AbstractMealItemHolder emptySpace(EmptyMealItemHolder emptyMealItemHolder) {
        return emptyMealItemHolder;
    }
}
