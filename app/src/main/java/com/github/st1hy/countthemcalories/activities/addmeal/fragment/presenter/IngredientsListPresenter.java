package com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.ingredientitems.IngredientListComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.ingredientitems.IngredientListModule;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.IngredientAction;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.MealIngredientsListModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealView;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.ingredientitems.IngredientItemViewHolder;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerViewNotifier;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.internal.Preconditions;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

@PerFragment
public class IngredientsListPresenter implements IngredientItemViewHolder.Callback, BasicLifecycle,
        RecyclerAdapterWrapper<IngredientItemViewHolder>  {

    @NonNull
    private final AddMealView view;
    @NonNull
    private final MealIngredientsListModel model;
    @NonNull
    private final PhysicalQuantitiesModel quantityModel;
    @NonNull
    private final IngredientListComponentFactory factory;

    private  RecyclerViewNotifier notifier;

    private final CompositeSubscription subscriptions = new CompositeSubscription();


    @Inject
    public IngredientsListPresenter(@NonNull AddMealView view,
                                    @NonNull MealIngredientsListModel model,
                                    @NonNull PhysicalQuantitiesModel quantityModel,
                                    @NonNull IngredientListComponentFactory factory) {
        this.view = view;
        this.model = model;
        this.quantityModel = quantityModel;
        this.factory = factory;
    }

    @Override
    public void setNotifier(@NonNull RecyclerViewNotifier viewNotifier) {
        this.notifier = viewNotifier;
    }

    @Override
    public void onStart() {
        notifier.notifyDataSetChanged();
        onDataSetChanged();
        subscribe(
                Observable.just(model.removeExtraIngredientType())
                        .filter(Functions.NOT_NULL)
                        .map(new Func1<IngredientTemplate, Ingredient>() {
                            @Override
                            public Ingredient call(IngredientTemplate ingredientTemplate) {
                                return new Ingredient(ingredientTemplate, BigDecimal.ZERO);
                            }
                        })
                        .mergeWith(
                                view.getAddIngredientButtonObservable()
                                        .flatMap(new Func1<Void, Observable<Ingredient>>() {
                                            @Override
                                            public Observable<Ingredient> call(Void aVoid) {
                                                return view.addIngredient();
                                            }
                                        })
                        )
                        .flatMap(new Func1<Ingredient, Observable<IngredientAction>>() {
                            @Override
                            public Observable<IngredientAction> call(Ingredient ingredient) {
                                return view.showIngredientDetails(-1L, ingredient, Collections.<Pair<View, String>>emptyList());
                            }
                        }).subscribe(onIngredientAction())
        );
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

    public int getItemViewType(int position) {
        return R.layout.add_meal_ingredient_item;
    }

    @Override
    public int getItemCount() {
        return model.getItemsCount();
    }

    @Override
    public void onIngredientClicked(@NonNull Ingredient ingredient, @NonNull final IngredientItemViewHolder viewHolder) {
        final int position = model.indexOf(ingredient);
        final List<Pair<View, String>> sharedViews = ImmutableList.of(
                pairOf(viewHolder.getImage(), "ingredient-shared-view-image")
//                pairOf(viewHolder.getRoot(), "ingredient-shared-view")
//                pairOf(viewHolder.getName(), "ingredient-shared-view-name")
//                pairOf(viewHolder.getCalories(), "ingredient-shared-view-calories"),
//                pairOf(viewHolder.getDensity(), "ingredient-shared-view-density")
        );
        viewHolder.setEnabled(false);
        subscribe(
                view.showIngredientDetails(position, ingredient, sharedViews)
                        .first()
                        .doOnNext(new Action1<IngredientAction>() {
                            @Override
                            public void call(IngredientAction ingredientAction) {
                                viewHolder.setEnabled(true);
                            }
                        })
                        .subscribe(onIngredientAction())
        );
    }

    @Override
    public IngredientItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        Preconditions.checkNotNull(view);
        return factory.newAddMealFragmentComponent(new IngredientListModule(view, this))
                .getHolder();
    }

    @Override
    public void onBindViewHolder(IngredientItemViewHolder holder, int position) {
        final Ingredient ingredient = model.getItemAt(position);
        holder.setIngredient(ingredient);
        IngredientTemplate type = ingredient.getIngredientTypeOrNull();
        holder.setName(type.getName());
        final EnergyDensity energyDensity = quantityModel.convertToPreferred(EnergyDensity.from(type));
        holder.setEnergyDensity(quantityModel.format(energyDensity));
        final AmountUnit amountUnit = energyDensity.getAmountUnit().getBaseUnit();
        final BigDecimal amount = quantityModel.convertAmountFromDatabase(ingredient.getAmount(), amountUnit);
        BigDecimal displayedAmount = amount.setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
        holder.setAmount(quantityModel.format(displayedAmount, amountUnit));
        holder.setCalorieCount(quantityModel.formatEnergyCount(amount, amountUnit, energyDensity));
        onBindImage(type, holder);
    }

    private void onBindImage(@NonNull IngredientTemplate ingredient,
                             @NonNull IngredientItemViewHolder holder) {
        holder.setImagePlaceholder(ingredient.getAmountType() == AmountUnitType.VOLUME ?
                R.drawable.ic_fizzy_drink : R.drawable.ic_fork_and_knife_wide);
        holder.setImageUri(ImageHolderDelegate.from(ingredient.getImageUri()));
    }

    private void subscribe(Subscription subscribe) {
        subscriptions.add(subscribe);
    }

    @NonNull
    private Subscriber<IngredientAction> onIngredientAction() {
        return new SimpleSubscriber<IngredientAction>() {
            @Override
            public void onNext(IngredientAction ingredientAction) {
                switch (ingredientAction.getType()) {
                    case EDIT:
                        onIngredientEditFinished(ingredientAction.getId(), ingredientAction.getIngredient());
                        break;
                    case REMOVE:
                        onIngredientRemoved(ingredientAction.getId());
                        break;
                    case CANCELED:
                        break;
                }
            }
        };
    }

    private void onIngredientRemoved(long requestId) {
        if (requestId == -1L) return;
        model.removeIngredient((int) requestId);
        notifier.notifyDataSetChanged();
        onDataSetChanged();
    }

    private void onIngredientEditFinished(long requestId, @NonNull Ingredient ingredient) {
        if (requestId == -1) {
            onIngredientAdded(ingredient);
        } else {
            onIngredientEdited(requestId, ingredient);
        }
    }

    private void onIngredientEdited(long requestId, @NonNull Ingredient ingredient) {
        int position = (int) requestId;
        model.modifyIngredient(position, ingredient);
        notifyChanged(position);
    }

    private void onIngredientAdded(@NonNull Ingredient ingredient) {
        int position = model.addIngredient(ingredient);
        notifyInserted(position);
    }

    private void notifyInserted(int position) {
        notifier.notifyItemInserted(position);
        view.scrollTo(position);
        view.showSnackbarError(Optional.<String>absent());
        onDataSetChanged();
    }

    private void notifyChanged(int position) {
        notifier.notifyItemChanged(position);
        onDataSetChanged();
    }

    private void onDataSetChanged() {
        setEmptyIngredientsVisibility();
        setTotalCalories();
    }

    private void setEmptyIngredientsVisibility() {
        view.setEmptyIngredientsVisibility(Visibility.of(getItemCount() == 0));
    }

    private void setTotalCalories() {
        Observable.from(model.getIngredients())
                .map(quantityModel.mapToEnergy())
                .map(quantityModel.sumAll())
                .lastOrDefault(BigDecimal.ZERO)
                .map(quantityModel.energyAsString())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String totalEnergy) {
                        view.setTotalEnergy(totalEnergy);
                    }
                });
    }

    @NonNull
    private static Pair<View, String> pairOf(@NonNull View view, @NonNull String string) {
        return Pair.create(view, string);
    }
}
