package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealFragment;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityModule;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.DaggerAddMealActivityComponent;
import com.github.st1hy.countthemcalories.core.Utils;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.Transformers;
import com.jakewharton.rxbinding.view.RxMenuItem;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.subjects.PublishSubject;

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
