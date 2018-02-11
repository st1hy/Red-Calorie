package com.github.st1hy.countthemcalories.ui.activities.mealdetail.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.ui.activities.mealdetail.fragment.view.MealDetailView;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.ui.core.viewcontrol.PostponeTransitions;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;

import javax.inject.Inject;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

@PerFragment
public class MealDetailPresenterImpl implements MealDetailPresenter {

    private final Meal meal;
    private final MealDetailView view;
    private final PhysicalQuantitiesModel quantitiesModel;
    private final ImageHolderDelegate imageHolderDelegate;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    PostponeTransitions postponeTransitions;

    @Inject
    public MealDetailPresenterImpl(@NonNull MealDetailView view,
                                   @NonNull Meal meal,
                                   @NonNull PhysicalQuantitiesModel quantitiesModel,
                                   @NonNull ImageHolderDelegate imageHolderDelegate) {
        this.view = view;
        this.meal = meal;
        this.quantitiesModel = quantitiesModel;
        this.imageHolderDelegate = imageHolderDelegate;
    }

    @Override
    public void onStart() {
        imageHolderDelegate.onAttached();
        subscriptions.add(postponeTransitions.postponeTransitions(imageHolderDelegate.getLoadingObservable()));
        setupView(meal);
        subscriptions.add(view.getEditObservable()
                .subscribe(aVoid -> view.editMealWithId(meal.getId()))
        );
        subscriptions.add(view.getDeleteObservable()
                .subscribe(aVoid -> view.deleteMealWithId(meal.getId()))
        );
        subscriptions.add(view.getCopyObservable()
                .subscribe(any -> view.copyMealWithId(meal.getId()))
        );
    }

    @Override
    public void onStop() {
        imageHolderDelegate.onDetached();
        subscriptions.clear();
    }

    private void setupView(@NonNull Meal meal) {
        view.setName(meal.getName());
        imageHolderDelegate.displayImage(meal.getImageUri());
        view.setDate(quantitiesModel.formatTime(meal.getCreationDate()));
        Observable.from(meal.getIngredients())
                .map(quantitiesModel.mapToEnergy())
                .map(quantitiesModel.sumAll())
                .lastOrDefault(0.0)
                .map(quantitiesModel.energyAsString())
                .subscribe(view::setEnergy);
    }

}
