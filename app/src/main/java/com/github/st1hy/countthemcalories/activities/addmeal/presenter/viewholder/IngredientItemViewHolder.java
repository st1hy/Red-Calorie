package com.github.st1hy.countthemcalories.activities.addmeal.presenter.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IngredientItemViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.add_meal_ingredient_item_name)
    TextView name;
    @Bind(R.id.add_meal_ingredient_energy_density)
    TextView energyDensity;
    @Bind(R.id.add_meal_ingredient_amount)
    TextView amount;
    @Bind(R.id.add_meal_ingredient_calorie_count)
    TextView calorieCount;
    @Bind(R.id.add_meal_ingredient_image_container)
    View imageContainer;
    @Bind(R.id.add_meal_ingredient_image)
    ImageView image;

    public IngredientItemViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setName(String name) {
        this.name.setText(name);
    }
}
