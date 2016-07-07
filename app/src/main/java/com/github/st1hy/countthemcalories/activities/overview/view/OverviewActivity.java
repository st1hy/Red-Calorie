package com.github.st1hy.countthemcalories.activities.overview.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.addmeal.view.EditMealActivity;
import com.github.st1hy.countthemcalories.activities.mealdetail.view.MealDetailActivity;
import com.github.st1hy.countthemcalories.activities.overview.fragment.view.OverviewFragment;
import com.github.st1hy.countthemcalories.activities.overview.inject.DaggerOverviewActivityComponent;
import com.github.st1hy.countthemcalories.activities.overview.inject.OverviewActivityComponent;
import com.github.st1hy.countthemcalories.activities.overview.inject.OverviewActivityModule;
import com.github.st1hy.countthemcalories.core.command.view.UndoDrawerActivity;
import com.github.st1hy.countthemcalories.database.parcel.MealParcel;
import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import timber.log.Timber;

public class OverviewActivity extends UndoDrawerActivity implements OverviewScreen {

    public static final int REQUEST_MEAL_DETAIL = 0x300;

    @BindView(R.id.overview_fab)
    FloatingActionButton fab;

    @BindView(R.id.overview_root)
    CoordinatorLayout root;
    @BindView(R.id.overview_total_energy)
    TextView totalEnergy;

    @Inject
    OverviewFragment content;

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
        Intent intent = new Intent(this, AddMealActivity.class);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Observable<Void> getOpenMealScreenObservable() {
        return RxView.clicks(fab);
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
        Intent intent = new Intent(this, EditMealActivity.class);
        intent.putExtra(EditMealActivity.EXTRA_MEAL_PARCEL, mealParcel);
        startActivity(intent);
    }

    @NonNull
    @Override
    protected View getUndoRoot() {
        return root;
    }


    @Override
    public void setTotalEnergy(@NonNull String energy) {
        totalEnergy.setText(energy);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == OverviewActivity.REQUEST_MEAL_DETAIL) {
            if (!handleResultMealDetail(resultCode, data)) {
                Timber.w("Incorrect result from meal detail activity, code:%d, data:%s", requestCode, data);
            }
        }
    }

    public boolean handleResultMealDetail(int resultCode, @Nullable Intent data) {
        if (data == null) return false;
        long mealId = data.getLongExtra(MealDetailActivity.EXTRA_RESULT_MEAL_ID_LONG, -2L);
        if (mealId == -2L) return false;
        switch (resultCode) {
            case MealDetailActivity.RESULT_EDIT:
                content.editMealWithId(mealId);
                break;
            case MealDetailActivity.RESULT_DELETE:
                content.deleteMealWithId(mealId);
                break;
            default: return false;
        }
        return true;
    }

}
