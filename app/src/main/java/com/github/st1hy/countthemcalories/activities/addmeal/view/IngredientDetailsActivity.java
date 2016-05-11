package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IngredientDetailsActivity extends BaseActivity {

    @Bind(R.id.add_meal_ingredient_accept)
    ImageButton accept;
    @Bind(R.id.add_meal_ingredient_remove)
    ImageButton remove;
    @Bind(R.id.add_meal_ingredient_edit_amount)
    EditText editAmount;


    @Bind(R.id.add_meal_ingredient_item_name)
    TextView name;
    @Bind(R.id.add_meal_ingredient_energy_density)
    TextView energyDensity;
    @Bind(R.id.add_meal_ingredient_amount)
    TextView amount;
    @Bind(R.id.add_meal_ingredient_calorie_count)
    TextView calorieCount;
    @Bind(R.id.add_meal_ingredient_image)
    ImageView image;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_meal_ingredient_item_extended);
        ButterKnife.bind(this);
        name.setText("Name");
    }
}
