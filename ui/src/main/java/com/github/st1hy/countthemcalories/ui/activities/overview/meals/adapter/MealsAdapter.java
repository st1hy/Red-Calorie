package com.github.st1hy.countthemcalories.ui.activities.overview.meals.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.adapter.holder.AbstractMealItemHolder;
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.adapter.holder.MealItemHolder;
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.adapter.inject.MealRowComponent;
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.adapter.inject.MealRowComponentFactory;
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.adapter.inject.MealRowModule;
import com.github.st1hy.countthemcalories.ui.contract.Ingredient;
import com.github.st1hy.countthemcalories.ui.contract.Meal;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

@PerFragment
public class MealsAdapter extends RecyclerView.Adapter<AbstractMealItemHolder> {

    private static final int mealItemLayout = R.layout.overview_item_scrolling;
    private static final int mealItemLayoutBottom = R.layout.list_item_bottom;
    private static final int mealItemLayoutTop = R.layout.list_item_top;
    private static final int TOP_PADDING = 1;
    private static final int PADDING = 2;

    private List<Meal> list = Collections.emptyList();

    @Inject
    PublishSubject<MealInteraction> interactionSubject;
    @Inject
    PhysicalQuantitiesModel quantityModel;
    @Inject
    MealRowComponentFactory mealRowComponentFactory;

    @Inject
    MealsAdapter() {
    }

    public void onNewDataSet(@NonNull List<Meal> meals) {
        this.list = meals;
    }

    public int getMealsCount() {
        return list.size();
    }

    public void addMealAtPosition(@NonNull Meal meal, int positionOnList) {
        list.add(positionOnList, meal);
        if (getMealsCount() > 1) {
            notifyItemInserted(positionOnAdapter(positionOnList));
        } else {
            notifyDataSetChanged();
        }
    }

    public void removeMealAtPosition(int positionOnList) {
        list.remove(positionOnList);
        if (getMealsCount() > 0) {
            notifyItemRemoved(positionOnAdapter(positionOnList));
        } else {
            notifyDataSetChanged();
        }
    }

    @Nullable
    public Pair<Integer, Meal> getMealPositionWithId(long mealId) {
        List<Meal> meals = this.list;
        for (int i = 0; i < meals.size(); i++) {
            Meal meal = meals.get(i);
            Long id = meal.getId();
            if (id != null && id == mealId) {
                return Pair.create(i, meal);
            }
        }
        return null;
    }

    @Override
    public int getItemCount() {
        int listSize = getMealsCount();
        return listSize > 0 ? listSize + PADDING : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < TOP_PADDING) {
            return mealItemLayoutTop;
        } else if (position < getMealsCount() + TOP_PADDING) {
            return mealItemLayout;
        } else {
            return mealItemLayoutBottom;
        }
    }

    @Override
    public AbstractMealItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MealRowComponent component = mealRowComponentFactory.newMealRowComponent(
                new MealRowModule(viewType, parent));
        if (viewType == mealItemLayout) {
            MealItemHolder holder = component.getMealHolder();
            holder.fillParent(parent);
            return holder;
        } else {
            return component.getSpaceHolder();
        }
    }

    @Override
    public void onBindViewHolder(AbstractMealItemHolder itemHolder, int position) {
        if (itemHolder instanceof MealItemHolder) {
            MealItemHolder holder = (MealItemHolder) itemHolder;
            final Meal meal = list.get(positionOnList(position));
            holder.setName(meal.getName());
            holder.setMeal(meal);
            holder.setDate(quantityModel.formatTime(meal.getCreationDate()));
            onBindIngredients(holder, meal);
            onBindImage(meal, holder);
            holder.setEnabled(true);
        }
    }

    @Override
    public void onViewAttachedToWindow(AbstractMealItemHolder itemHolder) {
        if (itemHolder instanceof MealItemHolder) {
            MealItemHolder holder = (MealItemHolder) itemHolder;
            holder.onAttached(interactionSubject);
        }
    }

    @Override
    public void onViewDetachedFromWindow(AbstractMealItemHolder itemHolder) {
        if (itemHolder instanceof MealItemHolder) {
            MealItemHolder holder = (MealItemHolder) itemHolder;
            holder.onDetached();
        }
    }


    private void onBindIngredients(@NonNull final MealItemHolder holder,
                                   @NonNull Meal meal) {
        List<Ingredient> ingredients = meal.getIngredients();
        Observable<Ingredient> ingredientObservable = Observable.from(ingredients);
        Observable<Double> decimalObservable = ingredientObservable
                .map(quantityModel.mapToEnergy()).cache();
        ingredientObservable.map(ingredient -> ingredient.getIngredientTypeOrNull().getDisplayName())
                .map(joinStrings())
                .lastOrDefault(new StringBuilder(0))
                .map(stringBuilder -> stringBuilder.toString().trim())
                .subscribe(holder::setIngredients);
        decimalObservable.map(quantityModel.sumAll())
                .lastOrDefault(0.0)
                .map(value -> DecimalFormat.getIntegerInstance().format(value))
                .doOnNext(any -> holder.setTotalEnergyUnit(quantityModel.getEnergyUnitName()))
                .subscribe(holder::setTotalEnergy);
    }

    private void onBindImage(@NonNull Meal meal, @NonNull MealItemHolder holder) {
        holder.setImageUri(meal.getImageUri());
    }

    @NonNull
    private static Func1<? super String, StringBuilder> joinStrings() {
        return new Func1<String, StringBuilder>() {
            StringBuilder builder = new StringBuilder(64);

            @Override
            public StringBuilder call(String s) {
                return builder.append(s).append('\n');
            }
        };
    }

    private static int positionOnList(int positionOnAdapter) {
        return positionOnAdapter - TOP_PADDING;
    }

    private static int positionOnAdapter(int positionOnList) {
        return positionOnList + TOP_PADDING;
    }
}
