package com.github.st1hy.countthemcalories.ui.activities.addmeal.model;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.view.View;

import com.github.st1hy.countthemcalories.database.Ingredient;

import java.util.List;

public class ShowIngredientsInfo {
    private final long id;
    @NonNull
    private final Ingredient ingredient;
    @NonNull
    private final List<Pair<View, String>> sharedElements;


    private ShowIngredientsInfo(long id, @NonNull Ingredient ingredient,
                                @NonNull List<Pair<View, String>> sharedElements) {
        this.id = id;
        this.ingredient = ingredient;
        this.sharedElements = sharedElements;
    }

    public static ShowIngredientsInfo of(long id, @NonNull Ingredient ingredient,
                                         @NonNull List<Pair<View, String>> sharedElements) {
        return new ShowIngredientsInfo(id, ingredient, sharedElements);
    }

    public long getId() {
        return id;
    }

    @NonNull
    public Ingredient getIngredient() {
        return ingredient;
    }

    @NonNull
    public List<Pair<View, String>> getSharedElements() {
        return sharedElements;
    }

    @Override
    public String toString() {
        return "ShowIngredientsInfo{" +
                "id=" + id +
                ", ingredient=" + ingredient +
                '}';
    }
}
