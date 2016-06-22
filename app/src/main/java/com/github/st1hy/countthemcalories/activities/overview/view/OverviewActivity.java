package com.github.st1hy.countthemcalories.activities.overview.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;
import com.github.st1hy.countthemcalories.activities.overview.inject.DaggerOverviewActivityComponent;
import com.github.st1hy.countthemcalories.activities.overview.inject.OverviewActivityComponent;
import com.github.st1hy.countthemcalories.activities.overview.inject.OverviewActivityModule;
import com.github.st1hy.countthemcalories.activities.overview.presenter.MealsAdapter;
import com.github.st1hy.countthemcalories.activities.overview.presenter.OverviewPresenter;
import com.github.st1hy.countthemcalories.core.command.view.UndoDrawerActivity;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.parcel.MealParcel;
import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class OverviewActivity extends UndoDrawerActivity implements OverviewView {
    public static final int REQUEST_MEAL_DETAIL = 0x300;

    @Inject
    OverviewPresenter presenter;
    @Inject
    MealsAdapter adapter;

    @BindView(R.id.overview_fab)
    FloatingActionButton fab;

    @BindView(R.id.overview_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.overview_total_energy)
    TextView totalEnergy;
    @BindView(R.id.overview_empty)
    View emptyList;
    @BindView(R.id.overview_empty_background)
    ImageView emptyBackground;
    @BindView(R.id.overview_root)
    CoordinatorLayout root;

    OverviewActivityComponent component;

    ActionBarDrawerToggle drawerToggle;


    @NonNull
    protected OverviewActivityComponent getComponent() {
        if (component == null) {
            component = DaggerOverviewActivityComponent.builder()
                    .applicationComponent(getAppComponent())
                    .overviewActivityModule(new OverviewActivityModule(this))
                    .build();
        }
        return component;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);
        ButterKnife.bind(this);
        getComponent().inject(this);
        onBind();
    }

    @Override
    protected void onBind() {
        super.onBind();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setTitle("");
        drawerToggle = createToggle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerToggle(drawerToggle);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterDrawerToggle(drawerToggle);
    }

    @Override
    public void openAddMealScreen() {
        startActivity(new Intent(this, AddMealActivity.class));
    }

    @NonNull
    @Override
    public Observable<Void> getOpenMealScreenObservable() {
        return RxView.clicks(fab);
    }

    @Override
    public void setTotalEnergy(@NonNull String energy) {
        totalEnergy.setText(energy);
    }

    @Override
    public void setEmptyListVisibility(@NonNull Visibility visibility) {
        //noinspection WrongConstant
        emptyList.setVisibility(visibility.getVisibility());
    }

    @Override
    public void openMealDetails(@NonNull MealParcel mealParcel, @NonNull View sharedView) {
        Bundle startOptions = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, sharedView, "overview-shared-view-image")
                .toBundle();
        Intent intent = new Intent(this, MealDetailActivity.class);
        intent.putExtra(MealDetailActivity.EXTRA_MEAL_PARCEL, mealParcel);
        startActivityForResult(intent, REQUEST_MEAL_DETAIL, startOptions);
    }

    @Override
    public void openEditMealScreen(@NonNull MealParcel mealParcel) {
        Intent intent = new Intent(this, AddMealActivity.class);
        intent.putExtra(AddMealActivity.EXTRA_MEAL_PARCEL, mealParcel);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!presenter.onActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @NonNull
    @Override
    protected View getUndoRoot() {
        return root;
    }


}
