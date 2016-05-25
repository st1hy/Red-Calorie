package com.github.st1hy.countthemcalories.activities.mealdetail.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.mealdetail.inject.DaggerMealDetailComponent;
import com.github.st1hy.countthemcalories.activities.mealdetail.inject.MealDetailComponent;
import com.github.st1hy.countthemcalories.activities.mealdetail.inject.MealDetailsModule;
import com.github.st1hy.countthemcalories.activities.mealdetail.presenter.MealDetailPresenter;
import com.github.st1hy.countthemcalories.activities.mealdetail.presenter.MealIngredientsAdapter;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class MealDetailActivity extends BaseActivity implements MealDetailView {
    public static final String EXTRA_MEAL_PARCEL = "meal detail parcel";
    public static final int RESULT_EDIT = 0x51;
    public static final int RESULT_DELETE = 0x52;
    public static final String EXTRA_RESULT_MEAL_ID_LONG = "extra result meal id";

    MealDetailComponent component;

    @Inject
    MealDetailPresenter presenter;
    @Inject
    MealIngredientsAdapter adapter;

    @BindView(R.id.overview_extended_name)
    TextView name;
    @BindView(R.id.overview_extended_date)
    TextView date;
    @BindView(R.id.overview_extended_energy_count)
    TextView energy;
    @BindView(R.id.overview_extended_image)
    ImageView image;
    @BindView(R.id.overview_extended_edit)
    View editButton;
    @BindView(R.id.overview_extended_remove)
    View removeButton;
    @BindView(R.id.overview_extended_recycler)
    RecyclerView recyclerView;

    @NonNull
    protected MealDetailComponent getComponent(@Nullable Bundle savedInstanceState) {
        if (component == null) {
            component = DaggerMealDetailComponent.builder()
                    .applicationComponent(getAppComponent())
                    .mealDetailsModule(new MealDetailsModule(this, savedInstanceState))
                    .build();
        }
        return component;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_item_extended);
        getComponent(savedInstanceState).inject(this);
        ButterKnife.bind(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveState(outState);
    }

    @Override
    public void setName(@NonNull String name) {
        this.name.setText(name);
    }

    @NonNull
    @Override
    public ImageView getImageView() {
        return image;
    }

    @NonNull
    @Override
    public Observable<Void> getEditObservable() {
        return RxView.clicks(editButton);
    }

    @Override
    public void setDate(@NonNull String date) {
        this.date.setText(date);
    }

    @Override
    public void setEnergy(@NonNull String energy) {
        this.energy.setText(energy);
    }

    @Override
    public Observable<Void> getDeleteObservable() {
        return RxView.clicks(removeButton);
    }

    @Override
    public void setResultAndFinish(int resultCode, @NonNull Intent data) {
        setResult(resultCode, data);
        ActivityCompat.finishAfterTransition(this);
    }

}
