package com.github.st1hy.countthemcalories.inject.module;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.github.st1hy.countthemcalories.AddMealActivity;
import com.github.st1hy.countthemcalories.OverviewActivity;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import dagger.Module;
import dagger.Provides;

@Module
public class AddMealActivityModule {
    private final AddMealActivity activity;

    @Bind(R.id.add_meal_toolbar)
    Toolbar toolbar;
    @Bind(R.id.add_meal_save_button)
    Button saveButton;

    public AddMealActivityModule(AddMealActivity activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);
    }

    @Provides
    @PerActivity
    public Activity provideActivity() {
        return activity;
    }

    @Provides
    @PerActivity
    public Toolbar provideToolbar() {
        return toolbar;
    }

    @Provides
    @PerActivity
    public Button provideSaveButton(@Named("save_button_action") View.OnClickListener saveButtonAction) {
        saveButton.setOnClickListener(saveButtonAction);
        return saveButton;
    }

    @Provides
    @Named("save_button_action")
    public View.OnClickListener provideSaveButtonAction(@Named("return_to_overview") final Intent returnToOverview) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(returnToOverview);
            }
        };
    }

    @Provides
    @Named("return_to_overview")
    public Intent provideSaveIntent() {
        Intent intent = new Intent(activity, OverviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }
}
