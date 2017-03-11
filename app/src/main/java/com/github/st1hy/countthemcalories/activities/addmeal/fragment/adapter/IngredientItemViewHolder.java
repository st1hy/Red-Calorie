package com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter;

import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter.inject.IngredientRootView;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.adapter.inject.PerIngredientRow;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static com.github.st1hy.countthemcalories.core.rx.Transformers.channel;

@PerIngredientRow
public class IngredientItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.add_meal_ingredient_item_name)
    TextView name;
    @BindView(R.id.add_meal_ingredient_energy_density)
    TextView energyDensity;
    @BindView(R.id.add_meal_ingredient_amount)
    TextView amount;
    @BindView(R.id.add_meal_ingredient_calorie_count)
    TextView calorieCount;
    @BindView(R.id.add_meal_ingredient_calorie_unit)
    TextView calorieUnit;
    @BindView(R.id.add_meal_ingredient_compact)
    ViewGroup compatView;

    @Inject
    PublishSubject<IngredientItemViewHolder> ingredientClicks;

    private Ingredient ingredient;
    @NonNull
    private final ImageHolderDelegate imageHolderDelegate;

    @NonNull
    private final ImageView image;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public IngredientItemViewHolder(@NonNull @IngredientRootView View itemView,
                                    @NonNull @Named("ingredientImageHolder") ImageHolderDelegate imageHolderDelegate,
                                    @NonNull @Named("ingredientImage") ImageView image) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.imageHolderDelegate = imageHolderDelegate;
        this.image = image;
    }

    public void setName(@NonNull String name) {
        this.name.setText(name);
    }

    public void setAmount(@NonNull String amount) {
        this.amount.setText(amount);
    }

    public void setCalorieCount(@NonNull String calorieCount) {
        this.calorieCount.setText(calorieCount);
    }

    public void setCalorieUnit(@NonNull String unit) {
        this.calorieUnit.setText(unit);
    }

    public void setEnergyDensity(@NonNull String energyDensity) {
        this.energyDensity.setText(energyDensity);
    }

    public void setIngredient(@NonNull Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    @NonNull
    @CheckResult
    public Observable<IngredientItemViewHolder> clicks() {
        return RxView.clicks(compatView)
                .map(Functions.into(this));
    }

    public void setImageUri(@NonNull Uri uri) {
        imageHolderDelegate.displayImage(uri);
    }

    public void setImagePlaceholder(@DrawableRes int placeholderResId) {
        imageHolderDelegate.setImagePlaceholder(placeholderResId);
    }

    public void onAttached() {
        imageHolderDelegate.onAttached();
        subscriptions.add(
                clicks().compose(channel(ingredientClicks))
                        .subscribe()
        );
    }

    public void onDetached() {
        imageHolderDelegate.onDetached();
        subscriptions.clear();
    }

    @NonNull
    public ImageView getImage() {
        return image;
    }

    public void setEnabled(boolean enabled) {
        compatView.setEnabled(enabled);
    }

}
