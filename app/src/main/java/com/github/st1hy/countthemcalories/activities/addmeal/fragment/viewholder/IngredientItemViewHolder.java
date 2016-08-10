package com.github.st1hy.countthemcalories.activities.addmeal.fragment.viewholder;

import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.google.common.base.Optional;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.internal.InstanceFactory;

public class IngredientItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.add_meal_ingredient_item_name)
    TextView name;
    @BindView(R.id.add_meal_ingredient_energy_density)
    TextView energyDensity;
    @BindView(R.id.add_meal_ingredient_amount)
    TextView amount;
    @BindView(R.id.add_meal_ingredient_calorie_count)
    TextView calorieCount;
    @BindView(R.id.add_meal_ingredient_image)
    ImageView image;

    @BindView(R.id.add_meal_ingredient_compact)
    ViewGroup compatView;
    @BindView(R.id.add_meal_ingredient_root)
    ViewGroup root;
    final Callback callback;
    Ingredient ingredient;

    final ImageHolderDelegate imageHolderDelegate;

    public IngredientItemViewHolder(@NonNull View itemView,
                                    @NonNull final Callback callback,
                                    @NonNull Picasso picasso,
                                    @NonNull PermissionsHelper permissionsHelper) {
        super(itemView);
        this.callback = callback;
        ButterKnife.bind(this, itemView);
        imageHolderDelegate = new ImageHolderDelegate(picasso, permissionsHelper,
                InstanceFactory.create(image));
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

    @OnClick(R.id.add_meal_ingredient_compact)
    public void onClicked() {
        if (ingredient != null) callback.onIngredientClicked(ingredient, this);
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
        imageHolderDelegate.setImageUri(uri);
    }

    public void setImagePlaceholder(@DrawableRes int placeholderResId) {
        imageHolderDelegate.setImagePlaceholder(placeholderResId);
    }

    public void onAttached() {
        imageHolderDelegate.onAttached();
    }

    public void onDetached() {
        imageHolderDelegate.onDetached();
    }

    @NonNull
    public ImageView getImage() {
        return image;
    }

    public void setEnabled(boolean enabled) {
        compatView.setEnabled(enabled);
    }

    public interface Callback {
        void onIngredientClicked(@NonNull Ingredient ingredient,
                                 @NonNull IngredientItemViewHolder viewHolder);
    }

}
