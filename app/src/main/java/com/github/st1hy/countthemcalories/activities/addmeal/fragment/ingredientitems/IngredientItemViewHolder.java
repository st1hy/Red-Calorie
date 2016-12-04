package com.github.st1hy.countthemcalories.activities.addmeal.fragment.ingredientitems;

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
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.ingredientitems.PerIngredientRow;
import com.google.common.base.Optional;
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
    @BindView(R.id.add_meal_ingredient_compact)
    ViewGroup compatView;
    @BindView(R.id.add_meal_ingredient_root)
    ViewGroup root;

    private Ingredient ingredient;

    @NonNull
    private final ImageHolderDelegate imageHolderDelegate;

    @NonNull
    private final ImageView image;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public IngredientItemViewHolder(@NonNull @Named("ingredientListRow") View itemView,
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

    @NonNull
    public View getRoot() {
        return root;
    }

    @NonNull
    public TextView getName() {
        return name;
    }

    @NonNull
    public TextView getCalories() {
        return calorieCount;
    }

    @NonNull
    public TextView getDensity() {
        return energyDensity;
    }

    public void setImageUri(@NonNull Optional<Uri> uri) {
        imageHolderDelegate.displayImage(uri);
    }

    public void setImagePlaceholder(@DrawableRes int placeholderResId) {
        imageHolderDelegate.setImagePlaceholder(placeholderResId);
    }

    public void onAttached(PublishSubject<IngredientItemViewHolder> ingredientClicks) {
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
