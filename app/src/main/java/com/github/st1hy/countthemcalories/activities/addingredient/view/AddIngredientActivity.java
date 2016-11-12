package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientModule;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.DaggerAddIngredientComponent;
import com.github.st1hy.countthemcalories.core.Utils;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.Transformers;
import com.jakewharton.rxbinding.view.RxMenuItem;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subjects.PublishSubject;

public class AddIngredientActivity extends BaseActivity {

    public static final String ARG_AMOUNT_UNIT = "amount unit type";
    public static final String ARG_EDIT_REQUEST_ID_LONG = "edit ingredient extra request id";
    public static final String ARG_EDIT_INGREDIENT_PARCEL = "edit ingredient extra parcel";
    public static final String ARG_EXTRA_NAME = "extra ingredient name";
    public static final String RESULT_INGREDIENT_TEMPLATE = "ingredient result";

    private AddIngredientComponent component;

    @BindView(R.id.image_header_toolbar)
    Toolbar toolbar;
    @Inject
    PublishSubject<AddIngredientMenuAction> menuActions;

    @NonNull
    protected AddIngredientComponent getComponent() {
        if (component == null) {
            component = DaggerAddIngredientComponent.builder()
                    .applicationComponent(getAppComponent())
                    .addIngredientModule(new AddIngredientModule(this))
                    .build();
        }
        return component;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ingredient_activity);
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
        getMenuInflater().inflate(R.menu.add_ingredient_menu, menu);
        MenuItem saveMenuItem = menu.findItem(R.id.action_save);
        RxMenuItem.clicks(saveMenuItem)
                .map(Functions.into(AddIngredientMenuAction.SAVE))
                .compose(Transformers.channel(menuActions))
                .subscribe();
        return true;
    }

}
