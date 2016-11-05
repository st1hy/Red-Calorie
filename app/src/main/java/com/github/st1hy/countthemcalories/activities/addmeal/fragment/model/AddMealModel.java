package com.github.st1hy.countthemcalories.activities.addmeal.fragment.model;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.fragment.model.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.core.picture.PictureModel;
import com.github.st1hy.countthemcalories.database.Meal;
import com.google.common.base.Optional;

import org.joda.time.DateTime;

import javax.inject.Inject;

import rx.Observable;

public class AddMealModel extends PictureModel {
    public static final String SAVED_MEAL_STATE = "add meal model";

    private final MealIngredientsListModel ingredientsListModel;
    private final RxMealsDatabaseModel databaseModel;
    private final Resources resources;

    private final Meal meal;

    @Inject
    public AddMealModel(@NonNull MealIngredientsListModel ingredientsListModel,
                        @NonNull RxMealsDatabaseModel databaseModel,
                        @NonNull Resources resources,
                        @NonNull Meal meal) {
        this.ingredientsListModel = ingredientsListModel;
        this.databaseModel = databaseModel;
        this.resources = resources;
        this.meal = meal;

    }

    @Override
    public int getImageSourceDialogTitleResId() {
        return R.string.add_meal_image_select_title;
    }

    @Override
    public int getSelectImageSourceOptions() {
        return R.array.add_meal_image_select_options;
    }

    @Override
    public int getSelectImageSourceAndRemoveOptions() {
        return R.array.add_meal_image_select_remove_options;
    }

    @Override
    public boolean hasImage() {
        return !Uri.EMPTY.equals(meal.getImageUri());
    }

    @NonNull
    public Observable<Void> saveToDatabase() {
        if (meal.getCreationDate() == null) meal.setCreationDate(DateTime.now());
        return databaseModel.insertOrUpdate(meal, ingredientsListModel.getIngredients());
    }

    @NonNull
    public Optional<String> getNameError() {
        return meal.getName().trim().isEmpty() ?
                Optional.of(resources.getString(R.string.add_meal_name_empty_error)) :
                Optional.<String>absent();
    }

    @NonNull
    public Optional<String> getIngredientsError() {
        if (ingredientsListModel.getItemsCount() == 0) {
            return Optional.of(resources.getString(R.string.add_meal_ingredients_empty_error));
        } else return Optional.absent();
    }
}
