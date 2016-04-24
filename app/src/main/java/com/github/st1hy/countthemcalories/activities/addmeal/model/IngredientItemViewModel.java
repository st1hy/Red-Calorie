package com.github.st1hy.countthemcalories.activities.addmeal.model;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IngredientItemViewModel extends RecyclerView.ViewHolder {

    @Bind(R.id.add_meal_ingredient_item_name)
    TextView name;

    public IngredientItemViewModel(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @NonNull
    public TextView getName() {
        return name;
    }
}
