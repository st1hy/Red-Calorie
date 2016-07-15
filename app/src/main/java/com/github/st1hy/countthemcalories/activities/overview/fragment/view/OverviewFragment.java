package com.github.st1hy.countthemcalories.activities.overview.fragment.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.fragment.inject.DaggerOverviewFragmentComponent;
import com.github.st1hy.countthemcalories.activities.overview.fragment.inject.OverviewFragmentComponent;
import com.github.st1hy.countthemcalories.activities.overview.fragment.inject.OverviewFragmentModule;
import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.MealsAdapter;
import com.github.st1hy.countthemcalories.activities.overview.fragment.presenter.OverviewPresenter;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewScreen;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.parcel.MealParcel;
import com.google.common.base.Preconditions;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class OverviewFragment extends BaseFragment implements OverviewView {

    @Inject
    OverviewPresenter presenter;
    @Inject
    MealsAdapter adapter;
    @Inject
    OverviewScreen overviewScreen;

    @BindView(R.id.overview_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.overview_empty)
    View emptyView;

    OverviewFragmentComponent component;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.overview_content, container, false);
    }

    @NonNull
    protected OverviewFragmentComponent getComponent() {
        if (component == null) {
            component = DaggerOverviewFragmentComponent.builder()
                    .applicationComponent(getAppComponent())
                    .overviewFragmentModule(new OverviewFragmentModule(this))
                    .build();
        }
        return component;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        ButterKnife.bind(this, Preconditions.checkNotNull(getView()));
        getComponent().inject(this);

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
    public void setEmptyListVisibility(@NonNull Visibility visibility) {
        //noinspection WrongConstant
        emptyView.setVisibility(visibility.getVisibility());
    }

    @Override
    @NonNull
    public Observable<Void> getOpenMealScreenObservable() {
        return overviewScreen.getOpenMealScreenObservable();
    }

    @Override
    public void openAddMealScreen() {
        overviewScreen.openAddMealScreen();
    }

    @Override
    public void openMealDetails(@NonNull MealParcel mealParcel, @NonNull View sharedView) {
        overviewScreen.openMealDetails(mealParcel, sharedView);
    }

    @Override
    public void openEditMealScreen(@NonNull MealParcel mealParcel) {
        overviewScreen.openEditMealScreen(mealParcel);
    }

    @Override
    @NonNull
    public Observable<Void> showUndoMessage(@StringRes int undoMessageResId) {
        return overviewScreen.showUndoMessage(undoMessageResId);
    }

    @Override
    public void hideUndoMessage() {
        overviewScreen.hideUndoMessage();
    }

    @Override
    public void setTotalEnergy(@NonNull String energy) {
        overviewScreen.setTotalEnergy(energy);
    }

    public void editMealWithId(long mealId) {
        adapter.editMealWithId(mealId);
    }

    public void deleteMealWithId(long mealId) {
        adapter.deleteMealWithId(mealId);
    }
}
