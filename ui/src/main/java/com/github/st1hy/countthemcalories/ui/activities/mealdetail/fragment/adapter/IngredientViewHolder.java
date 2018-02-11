package com.github.st1hy.countthemcalories.ui.activities.mealdetail.fragment.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.meal_detail_ingredient_item_name)
    TextView name;
    @BindView(R.id.meal_detail_ingredient_item_amount)
    TextView amount;
    @BindView(R.id.meal_detail_ingredient_item_energy)
    TextView energy;


    public IngredientViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setName(@NonNull String name) {
        this.name.setText(name);
    }

    public void setEnergy(@NonNull String energyCount) {
        this.energy.setText(energyCount);
    }

    public void setAmount(@NonNull String amount) {
        this.amount.setText(amount);
    }
}
