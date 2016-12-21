package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailView;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import java.math.BigDecimal;

import javax.inject.Inject;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

import static com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate.from;

@PerFragment
public class MealDetailPresenterImpl implements MealDetailPresenter {

    private final Meal meal;
    private final MealDetailView view;
    private final PhysicalQuantitiesModel quantitiesModel;
    private final ImageHolderDelegate imageHolderDelegate;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

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
        setupView(meal);
        subscriptions.add(view.getEditObservable()
                .subscribe(aVoid -> view.editMealWithId(meal.getId()))
        );
        subscriptions.add(view.getDeleteObservable()
                .subscribe(aVoid -> view.deleteMealWithId(meal.getId()))
        );
    }

    @Override
    public void onStop() {
        imageHolderDelegate.onDetached();
        subscriptions.clear();
    }

    private void setupView(@NonNull Meal meal) {
        view.setName(meal.getName());
        bindImage(meal);
        view.setDate(quantitiesModel.formatTime(meal.getCreationDate()));
        Observable.from(meal.getIngredients())
                .map(quantitiesModel.mapToEnergy())
                .map(quantitiesModel.sumAll())
                .lastOrDefault(BigDecimal.ZERO)
                .map(quantitiesModel.energyAsString())
                .subscribe(view::setEnergy);
    }

    void bindImage(@NonNull Meal meal) {
        imageHolderDelegate.displayImage(from(meal.getImageUri()));
    }

}
