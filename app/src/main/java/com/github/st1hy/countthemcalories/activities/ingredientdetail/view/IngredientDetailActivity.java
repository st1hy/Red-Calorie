package com.github.st1hy.countthemcalories.activities.ingredientdetail.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.inject.DaggerIngredientDetailComponent;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.inject.IngredientDetailComponent;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.inject.IngredientDetailModule;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.database.Ingredient;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_ID_LONG;

public class IngredientDetailActivity extends BaseActivity {

    public static final int RESULT_REMOVE = 0x200;

    IngredientDetailComponent component;

    @Inject
    IngredientDetailFragment content; //adds fragment to view hierarchy

    @NonNull
    protected IngredientDetailComponent getComponent() {
        if (component == null) {
            component = DaggerIngredientDetailComponent.builder()
                    .applicationComponent(getAppComponent())
                    .ingredientDetailModule(new IngredientDetailModule(this))
                    .build();
        }
        return component;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_detail_activity);
        getComponent().inject(this);
    }



}
