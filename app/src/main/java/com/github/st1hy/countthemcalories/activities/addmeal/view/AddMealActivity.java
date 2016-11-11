package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.IngredientAction;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.IngredientAction.Type;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealFragment;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityModule;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.DaggerAddMealActivityComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.model.EditIngredientResult;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.core.Utils;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.QueueSubject;
import com.github.st1hy.countthemcalories.core.headerpicture.view.WithPictureActivity;
import com.github.st1hy.countthemcalories.core.rx.Transformers;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.google.common.base.Optional;
import com.jakewharton.rxbinding.view.RxMenuItem;
import com.jakewharton.rxbinding.view.RxView;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_ID_LONG;

public class AddMealActivity extends BaseActivity {

    @BindView(R.id.image_header_toolbar)
    Toolbar toolbar;

    AddMealActivityComponent component;

    @Inject
    AddMealFragment content; //adds fragment to stack
    @Inject
    PublishSubject<AddMealMenuAction> menuActionPublishSubject;

    @NonNull
    protected AddMealActivityComponent getComponent() {
        if (component == null) {
            component = DaggerAddMealActivityComponent.builder()
                    .applicationComponent(getAppComponent())
                    .addMealActivityModule(new AddMealActivityModule(this))
                    .build();
        }
        return component;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_meal_activity);
        ButterKnife.bind(this);
        getComponent().inject(this);
        setSupportActionBar(toolbar);
        Utils.assertNotNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_meal_menu, menu);
        RxMenuItem.clicks(menu.findItem(R.id.action_save))
                .map(Functions.into(AddMealMenuAction.SAVE))
                .compose(Transformers.channel(menuActionPublishSubject))
                .subscribe();
        return true;
    }

}
