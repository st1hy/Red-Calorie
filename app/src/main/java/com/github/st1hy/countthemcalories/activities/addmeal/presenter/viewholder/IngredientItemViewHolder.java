package com.github.st1hy.countthemcalories.activities.addmeal.presenter.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    int position;
    final Callback callback;


    public IngredientItemViewHolder(@NonNull View itemView,
                                    @NonNull final Callback callback) {
        super(itemView);
        this.callback = callback;
        ButterKnife.bind(this, itemView);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClicked();
            }
        });
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

    public void setPosition(int position) {
        this.position = position;
    }

    @NonNull
    public ImageView getImage() {
        return image;
    }

    void onItemClicked() {
        callback.onIngredientClicked(root, position);
    }

    public interface Callback {
        void onIngredientClicked(@NonNull View sharedIngredientCompact, int position);
    }

}
