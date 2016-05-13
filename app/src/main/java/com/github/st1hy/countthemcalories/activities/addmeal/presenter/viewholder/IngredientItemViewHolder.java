package com.github.st1hy.countthemcalories.activities.addmeal.presenter.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.database.Ingredient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    public IngredientItemViewHolder(@NonNull View itemView,
                                    @NonNull final Callback callback) {
        super(itemView);
        this.callback = callback;
        ButterKnife.bind(this, itemView);
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

    @NonNull
    public ImageView getImage() {
        return image;
    }

    @OnClick(R.id.add_meal_ingredient_root)
    public void onClicked() {
        if (ingredient != null) callback.onIngredientClicked(root, ingredient);
    }

    public interface Callback {
        void onIngredientClicked(@NonNull View sharedIngredientCompact,
                                 @NonNull Ingredient ingredient);
    }

}
