package com.github.st1hy.countthemcalories.ui.activities.addmeal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.view.AddMealMenuAction;
import com.github.st1hy.countthemcalories.ui.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.ui.core.rx.Functions;
import com.github.st1hy.countthemcalories.ui.core.rx.Transformers;
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule;
import com.google.common.base.Preconditions;
import com.jakewharton.rxbinding.view.RxMenuItem;

import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import rx.subjects.PublishSubject;

public class AddMealActivity extends BaseActivity {

    public static final String EXTRA_MEAL_PARCEL = "edit meal parcel";
    public static final String EXTRA_INGREDIENT_PARCEL = "edit ingredient parcel";
    public static final String EXTRA_NEW_MEAL_DATE = "new meal date parcel";

    @BindView(R.id.image_header_toolbar)
    @Inject
    Toolbar toolbar;
    @Inject
    Map<String, Fragment> fragments; //adds fragments to stack
    @Inject
    PublishSubject<AddMealMenuAction> menuActionPublishSubject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_meal_activity);
        getAppComponent().newAddMealActivityComponent(new ActivityModule(this))
                .inject(this);
        setSupportActionBar(toolbar);
        Preconditions.checkNotNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
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
