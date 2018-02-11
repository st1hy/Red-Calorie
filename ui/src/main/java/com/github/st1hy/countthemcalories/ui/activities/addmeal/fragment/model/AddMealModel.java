package com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.model;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.database.rx.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.PictureModel;
import com.github.st1hy.countthemcalories.ui.core.meals.DefaultMealName;
import com.github.st1hy.countthemcalories.ui.core.time.DateTimeUtils;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.datetime.NewMealDate;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

import org.joda.time.DateTime;

import javax.inject.Inject;

import rx.Observable;

@PerFragment
@SuppressWarnings("Guava")
public class AddMealModel implements PictureModel {
    public static final String SAVED_MEAL_STATE = "add meal model";

    private final MealIngredientsListModel ingredientsListModel;
    private final RxMealsDatabaseModel databaseModel;
    private final Resources resources;
    @NonNull
    private final DefaultMealName defaultMealName;
    @NonNull
    private final PhysicalQuantitiesModel quantitiesModel;
    @NonNull
    private final DateTimeUtils dateTimeUtils;
    @Inject
    @Nullable
    @NewMealDate
    DateTime mealDateDay;

    private final Meal meal;
    private boolean isImageAvailableOverride;

    @Inject
    public AddMealModel(@NonNull MealIngredientsListModel ingredientsListModel,
                        @NonNull RxMealsDatabaseModel databaseModel,
                        @NonNull Resources resources,
                        @NonNull DefaultMealName defaultMealName,
                        @NonNull PhysicalQuantitiesModel quantitiesModel,
                        @NonNull DateTimeUtils dateTimeUtils,
                        @NonNull Meal meal) {
        this.ingredientsListModel = ingredientsListModel;
        this.databaseModel = databaseModel;
        this.resources = resources;
        this.defaultMealName = defaultMealName;
        this.quantitiesModel = quantitiesModel;
        this.dateTimeUtils = dateTimeUtils;
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
        return isImageAvailableOverride() && !Uri.EMPTY.equals(meal.getImageUri());
    }

    @Override
    public void setImageAvailableOverride(boolean isAvailable) {
        isImageAvailableOverride = isAvailable;
    }

    public boolean isImageAvailableOverride() {
        return isImageAvailableOverride;
    }

    @NonNull
    public Observable<Meal> saveToDatabase() {
        if (meal.getCreationDate() == null) {
            if (meal.getCreationDate() == null) {
                meal.setCreationDate(getDisplayTime());
            }
        }
        if (Strings.isNullOrEmpty(meal.getName())) {
            meal.setName(defaultMealName.getBestGuessMealNameAt(meal.getCreationDate()));
        }
        return databaseModel.insertOrUpdate(meal, ingredientsListModel.getIngredients());
    }

    @NonNull
    public Optional<String> getIngredientsError() {
        if (ingredientsListModel.getItemsCount() == 0) {
            return Optional.of(resources.getString(R.string.add_meal_ingredients_empty_error));
        } else return Optional.absent();
    }

    @Override
    public void setImageUri(@NonNull Uri uri) {
        meal.setImageUri(uri);
    }

    @NonNull
    public String getMealNameHint() {
        return defaultMealName.getBestGuessMealNameAt(getDisplayTime());
    }

    @NonNull
    public String formatTime(DateTime dateTime) {
        return quantitiesModel.formatTime(dateTime);
    }

    public long getTimeToNextMinuteMils() {
        return dateTimeUtils.getMillisToNextFullMinute();
    }

    @NonNull
    public DateTime getDisplayTime() {
        DateTime creationDate = meal.getCreationDate();
        if (creationDate == null) {
            DateTime now = DateTime.now();
            if (mealDateDay == null) {
                creationDate = now;
            } else {
                creationDate = mealDateDay.withTime(now.toLocalTime());
            }
        }
        return creationDate;
    }
}
