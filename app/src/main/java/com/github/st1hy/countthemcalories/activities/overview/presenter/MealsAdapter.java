package com.github.st1hy.countthemcalories.activities.overview.presenter;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDatabaseModel;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewView;
import com.github.st1hy.countthemcalories.activities.overview.view.viewholder.AbstractMealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.view.viewholder.EmptyMealItemHolder;
import com.github.st1hy.countthemcalories.activities.overview.view.viewholder.MealItemHolder;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.rx.Schedulers;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.Meal;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import dagger.internal.Preconditions;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MealsAdapter extends RecyclerView.Adapter<AbstractMealItemHolder> {
    private static final int mealItemLayout = R.layout.overview_item;
    private static final int bottomSpaceLayout = R.layout.overview_item_bottom_space;

    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private final MealDatabaseModel model;
    private final Picasso picasso;
    private final PhysicalQuantitiesModel quantityModel;
    private final OverviewView view;

    private List<Meal> list = Collections.emptyList();

    public MealsAdapter(OverviewView view, @NonNull MealDatabaseModel databaseModel, @NonNull Picasso picasso,
                        @NonNull PhysicalQuantitiesModel quantityModel) {
        this.view = view;
        this.model = databaseModel;
        this.picasso = picasso;
        this.quantityModel = quantityModel;
    }

    public void onStart() {
        loadTodayMeals();
    }

    private void loadTodayMeals() {
        DateTime now = DateTime.now();
        DateTime from = now.withTime(0, 0, 0, 0);
        DateTime to = now.withTime(23, 59, 59, 999);
        subscriptions.add(model.getAllFiltered(from, to)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new OnCursor()));
    }

    public void onStop() {
        subscriptions.clear();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getDaoItemCount()) {
            return mealItemLayout;
        } else {
            return bottomSpaceLayout;
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }


    @Override
    public AbstractMealItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        Preconditions.checkNotNull(view);
        if (viewType == mealItemLayout) {
            return new MealItemHolder(view);
        } else {
            return new EmptyMealItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(AbstractMealItemHolder holder, int position) {
        if (holder instanceof MealItemHolder) {
            onBindMealItemHolder((MealItemHolder) holder, position);
        }
    }

    private void onBindMealItemHolder(@NonNull MealItemHolder holder, int position) {
        final Meal meal = list.get(position);
        holder.setName(meal.getName());
        onBindIngredients(holder, meal.getIngredients());
        onBindImage(meal, holder);
    }

    private void onBindIngredients(@NonNull final MealItemHolder holder, @NonNull List<Ingredient> ingredients) {
        Observable<Ingredient> ingredientObservable = Observable.from(ingredients);
        Observable<BigDecimal> decimalObservable = ingredientObservable
                .map(quantityModel.mapToEnergy()).cache();
        ingredientObservable.map(toNames())
//                .zipWith(
//                        decimalObservable
//                                .map(quantityModel.setScale(0))
//                                .map(quantityModel.energyAsString()),
//                        joinIngredientWithEnergy()
//                )
                .map(joinStrings())
                .lastOrDefault(new StringBuilder(0))
                .subscribe(new Action1<StringBuilder>() {
                    @Override
                    public void call(StringBuilder stringBuilder) {
                        holder.setIngredients(stringBuilder.toString().trim());
                    }
                });
        decimalObservable.map(quantityModel.sumAll())
                .lastOrDefault(BigDecimal.ZERO)
                .map(quantityModel.setScale(0))
                .map(quantityModel.energyAsString())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String totalEnergy) {
                        holder.setTotalEnergy(totalEnergy);
                    }
                });
    }

    void onBindImage(@NonNull Meal meal, @NonNull MealItemHolder holder) {
        picasso.cancelRequest(holder.getImage());
        Uri imageUri = meal.getImageUri();
        if (imageUri != null && !imageUri.equals(Uri.EMPTY)) {
            RxPicasso.Builder.with(picasso, imageUri)
                    .centerCrop()
                    .fit()
                    .into(holder.getImage());
        } else {
            holder.getImage().setImageResource(R.drawable.ic_fork_and_knife_wide);
        }
    }

    private void onNewDataSet(@NonNull List<Meal> meals) {
        this.list = meals;
        notifyDataSetChanged();
        showTotal();
        setEmptyListVisibility();
    }

    private void showTotal() {
        subscriptions.add(Observable.from(list)
                .subscribeOn(Schedulers.computation())
                .flatMap(new Func1<Meal, Observable<Ingredient>>() {
                    @Override
                    public Observable<Ingredient> call(Meal meal) {
                        return Observable.from(meal.getIngredients());
                    }
                })
                .map(quantityModel.mapToEnergy())
                .map(quantityModel.sumAll())
                .lastOrDefault(BigDecimal.ZERO)
                .map(quantityModel.setScale(0))
                .map(quantityModel.energyAsString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String energy) {
                        view.setTotalEnergy(energy);
                    }
                }));

    }

    private void setEmptyListVisibility() {
        Visibility emptyList, emptyListVariation;
        if (list.size() == 0) {
            if (new Random().nextInt(100) == 0) {
                emptyList = Visibility.GONE;
                emptyListVariation = Visibility.VISIBLE;
            } else {
                emptyList = Visibility.VISIBLE;
                emptyListVariation = Visibility.GONE;
            }
        } else {
            emptyList = Visibility.GONE;
            emptyListVariation = Visibility.GONE;
        }
        view.setEmptyListVisibility(emptyList);
        view.setEmptyListVariationVisibility(emptyListVariation);
    }

    private int getDaoItemCount() {
        return list.size();
    }

    @NonNull
    private Func1<Ingredient, String> toNames() {
        return new Func1<Ingredient, String>() {
            @Override
            public String call(Ingredient ingredient) {
                return ingredient.getIngredientType().getName();
            }
        };
    }

    @NonNull
    private Func2<String, String, String> joinIngredientWithEnergy() {
        return new Func2<String, String, String>() {

            @Override
            public String call(String name, String energy) {
                return name + ": " + energy;
            }
        };
    }

    @NonNull
    private Func1<? super String, StringBuilder> joinStrings() {
        return new Func1<String, StringBuilder>() {
            StringBuilder builder = new StringBuilder(64);

            @Override
            public StringBuilder call(String s) {
                return builder.append(s).append('\n');
            }
        };
    }

    private class OnCursor extends Subscriber<List<Meal>> {

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            Timber.e(e, "Meal ingredients failed!");
        }

        @Override
        public void onNext(List<Meal> meals) {
            onNewDataSet(meals);
        }

    }
}
