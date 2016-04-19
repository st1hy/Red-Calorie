package com.github.st1hy.countthemcalories;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import com.github.st1hy.countthemcalories.inject.component.AddMealActivityComponent;
import com.github.st1hy.countthemcalories.inject.component.DaggerAddMealActivityComponent;
import com.github.st1hy.countthemcalories.inject.module.AddMealActivityModule;
import com.github.st1hy.countthemcalories.ui.BaseActivity;

import javax.inject.Inject;

public class AddMealActivity extends BaseActivity {

    @Inject
    Toolbar toolbar;

    private AddMealActivityComponent component;

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
        setContentView(R.layout.activity_add_meal);
        getComponent().inject(this);
        setSupportActionBar(toolbar);
        assertNotNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }
}
