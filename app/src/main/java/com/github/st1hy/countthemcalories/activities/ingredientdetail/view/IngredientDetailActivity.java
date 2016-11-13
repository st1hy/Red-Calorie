package com.github.st1hy.countthemcalories.activities.ingredientdetail.view;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.inject.DaggerIngredientDetailComponent;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.inject.IngredientDetailComponent;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.inject.IngredientDetailModule;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;

import javax.inject.Inject;

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
