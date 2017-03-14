package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailScreen;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.quantifier.view.FragmentRootView;
import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

@PerFragment
public class MealDetailViewImpl implements MealDetailView, MealDetailScreen {

    @NonNull
    private final MealDetailScreen screen;

    @BindView(R.id.meal_detail_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.meal_detail_name)
    TextView name;
    @BindView(R.id.meal_detail_date)
    TextView date;
    @BindView(R.id.meal_detail_energy_count)
    TextView energy;
    @BindView(R.id.meal_detail_edit)
    View editButton;
    @BindView(R.id.meal_detail_remove)
    View removeButton;

    @Inject
    public MealDetailViewImpl(@NonNull @FragmentRootView View rootView,
                              @NonNull MealDetailScreen screen,
                              @NonNull RecyclerView recyclerView) {
        this.screen = screen;
        this.recyclerView = recyclerView;
        ButterKnife.bind(this, rootView);
    }

    @Override
    public void setName(@NonNull String name) {
        this.name.setText(name);
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
    @NonNull
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
