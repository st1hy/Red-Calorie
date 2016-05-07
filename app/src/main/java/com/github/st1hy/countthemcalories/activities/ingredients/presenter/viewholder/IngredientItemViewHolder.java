package com.github.st1hy.countthemcalories.activities.ingredients.presenter.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.adapter.callbacks.OnItemInteraction;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IngredientItemViewHolder extends IngredientViewHolder implements View.OnClickListener {

    @Bind(R.id.ingredients_item_button)
    View ingredientButton;
    @Bind(R.id.ingredients_item_name)
    TextView name;
    @Bind(R.id.ingredients_item_energy_density)
    TextView energyDensity;
    @Bind(R.id.ingredients_item_image)
    ImageView image;

    private final IngredientTemplate reusableIngredient = new IngredientTemplate();
    private final OnItemInteraction<IngredientTemplate> callback;

    public IngredientItemViewHolder(@NonNull View itemView,
                                    @NonNull OnItemInteraction<IngredientTemplate> interaction) {
        super(itemView);
        this.callback = interaction;
        ButterKnife.bind(this, itemView);
        ingredientButton.setOnClickListener(this);
    }

    @NonNull
    public IngredientTemplate getReusableIngredient() {
        return reusableIngredient;
    }

    public void setName(@NonNull String name) {
        this.name.setText(name);
    }

    public void setEnergyDensity(@NonNull String value) {
        this.energyDensity.setText(value);
    }

    @NonNull
    public ImageView getImage() {
        return image;
    }

    @Override
    public void onClick(View v) {
        callback.onItemClicked(reusableIngredient);
    }
}
