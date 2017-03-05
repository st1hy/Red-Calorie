package com.github.st1hy.countthemcalories.activities.addmeal;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.AddMealFragment;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealMenuAction;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.Transformers;
import com.github.st1hy.countthemcalories.inject.common.ActivityModule;
import com.google.common.base.Preconditions;
import com.jakewharton.rxbinding.view.RxMenuItem;

import javax.inject.Inject;

import butterknife.BindView;
import rx.subjects.PublishSubject;

public class AddMealActivity extends BaseActivity {

    @BindView(R.id.image_header_toolbar)
    @Inject
    Toolbar toolbar;
    @Inject
    AddMealFragment content; //adds fragment to stack
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
