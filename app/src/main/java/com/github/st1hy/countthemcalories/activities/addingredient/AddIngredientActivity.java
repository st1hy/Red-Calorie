package com.github.st1hy.countthemcalories.activities.addingredient;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.AddIngredientFragment;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientMenuAction;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.Transformers;
import com.github.st1hy.countthemcalories.inject.activities.addingredient.AddIngredientModule;
import com.jakewharton.rxbinding.view.RxMenuItem;

import javax.inject.Inject;

import rx.subjects.PublishSubject;

public class AddIngredientActivity extends BaseActivity {

    public static final String ARG_EDIT_REQUEST_ID_LONG = "edit ingredient extra request id";
    public static final String ARG_EDIT_INGREDIENT_PARCEL = "edit ingredient extra parcel";
    public static final String ARG_EXTRA_NAME = "extra ingredient name";
    public static final String RESULT_INGREDIENT_TEMPLATE = "ingredient result";

    @Inject
    AddIngredientFragment content; //injects fragment
    @Inject
    Toolbar toolbar; //setup toolbar
    @Inject
    PublishSubject<AddIngredientMenuAction> menuActions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ingredient_activity);
        getAppComponent().newAddIngredientActivityComponent(new AddIngredientModule(this))
                .inject(this);
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
