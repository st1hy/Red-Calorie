package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.inject.DaggerMealDetailComponent;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.inject.MealDetailComponent;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.inject.MealDetailsModule;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter.MealDetailPresenter;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter.MealIngredientsAdapter;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailScreen;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.google.common.base.Preconditions;
import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class MealDetailFragment extends BaseFragment implements MealDetailView {

    public static final String ARG_MEAL_PARCEL = "meal detail parcel";

    MealDetailComponent component;

    @Inject
    MealDetailPresenter presenter;
    @Inject
    MealIngredientsAdapter adapter;
    @Inject
    MealDetailScreen screen;

    @BindView(R.id.meal_detail_name)
    TextView name;
    @BindView(R.id.meal_detail_date)
    TextView date;
    @BindView(R.id.meal_detail_energy_count)
    TextView energy;
    @BindView(R.id.meal_detail_image)
    ImageView image;
    @BindView(R.id.meal_detail_edit)
    View editButton;
    @BindView(R.id.meal_detail_remove)
    View removeButton;
    @BindView(R.id.meal_detail_recycler)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.meal_detail_content, container, false);
    }

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        ButterKnife.bind(this, Preconditions.checkNotNull(getView()));
        getComponent(savedInstanceState).inject(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveState(outState);
    }

    @Override
    public void setName(@NonNull String name) {
        this.name.setText(name);
    }

    @NonNull
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
    public void editMealWithId(long mealId) {
        screen.editMealWithId(mealId);
    }

    @Override
    public void deleteMealWithId(long mealId) {
        screen.deleteMealWithId(mealId);
    }
}
