package com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.presenter;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;

import com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.model.AddIngredientModel;
import com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.model.AddIngredientModelHelper;
import com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.model.IngredientTagsModel;
import com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.model.IngredientTypeCreateError;
import com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.model.InputType;
import com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.view.AddIngredientView;
import com.github.st1hy.countthemcalories.ui.activities.addingredient.view.AddIngredientMenuAction;
import com.github.st1hy.countthemcalories.ui.activities.settings.model.unit.AmountUnit;
import com.github.st1hy.countthemcalories.ui.activities.tags.model.Tags;
import com.github.st1hy.countthemcalories.ui.core.dialog.DialogView;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.SelectPicturePresenter;
import com.github.st1hy.countthemcalories.ui.core.rx.Filters;
import com.github.st1hy.countthemcalories.ui.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.ui.core.state.Visibility;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;

@SuppressWarnings("Guava")
@PerFragment
public class AddIngredientPresenterImp implements AddIngredientPresenter {

    @Inject
    AddIngredientView view;
    @Inject
    AddIngredientModel model;
    @Inject
    AddIngredientModelHelper modelHelper;
    @Inject
    SelectPicturePresenter picturePresenter;
    @Inject
    DialogView dialogView;
    @Inject
    Observable<AddIngredientMenuAction> menuActionObservable;
    @Inject
    IngredientTagsModel tagsModel;
    @Inject
    RecyclerView.Adapter adapter;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    AddIngredientPresenterImp() {
    }

    @Override
    public void onStart() {
        picturePresenter.loadImageUri(model.getImageUri());
        view.setName(model.getName());
        view.setEnergyDensityValue(model.getEnergyValue());
        view.setSelectedUnitName(modelHelper.getEnergyDensityUnitName());

        final Observable<CharSequence> nameObservable = view.getNameObservable();
        subscribe(
                nameObservable.subscribe(c -> model.setName(c.toString()))
        );
        final Observable<CharSequence> valueObservable = view.getValueObservable();
        subscribe(
                valueObservable.subscribe(c -> model.setEnergyValue(c.toString()))
        );

        subscribe(
                Observable.combineLatest(nameObservable, valueObservable,
                        (Func2<CharSequence, CharSequence, Void>) (name, energyValue) -> {
                            onCreateIngredientResult(modelHelper.canCreateIngredient(
                                    name.toString(), energyValue.toString())
                            );
                            return null;
                        })
                        .subscribe()
        );

        subscribe(
                menuActionObservable.filter(Filters.equalTo(AddIngredientMenuAction.SAVE))
                        .map(any -> modelHelper.canCreateIngredient())
                        .doOnNext(this::onCreateIngredientResult)
                        .filter(List::isEmpty)
                        .flatMap(any -> modelHelper.saveIntoDatabase())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::onIngredientTemplateCreated)
        );
        subscribe(
                view.getSearchObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aVoid -> {
                            String name = model.getName();
                            if (!name.trim().isEmpty())
                                view.showInWebBrowser(modelHelper.getSearchIngredientQuery(name));
                        })
        );
        subscribe(
                view.getSelectTypeObservable()
                        .flatMap(aVoid -> {
                            final List<Pair<AmountUnit, CharSequence>> optionsList = modelHelper.getSelectTypeDialogOptions();
                            CharSequence[] options = Collections2.transform(optionsList, input -> input.second)
                                    .toArray(new CharSequence[optionsList.size()]);
                            return dialogView.showAlertDialog(model.getSelectTypeDialogTitle(), options)
                                    .map(position -> optionsList.get(position).first);
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new SimpleSubscriber<AmountUnit>() {
                            @Override
                            public void onNext(AmountUnit amountUnit) {
                                model.setAmountType(amountUnit.getType());
                                view.setSelectedUnitName(modelHelper.getEnergyDensityUnitName());
                            }
                        })
        );
        onAdapterStart();
    }

    private void onAdapterStart() {
        subscriptions.add(
                view.addTagObservable()
                        .map(aVoid -> new Tags(tagsModel.copyTags()))
                        .compose(view.selectTags())
                        .subscribe(tags -> {
                            tagsModel.replaceWith(tags);
                            adapter.notifyDataSetChanged();
                        })
        );
        subscriptions.add(
                tagsModel.getTagsSizeObservable()
                        .map(size -> Visibility.of(size == 0))
                        .subscribe(view::setNoCategoriesVisibility)
        );
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

    private void onCreateIngredientResult(@NonNull List<IngredientTypeCreateError> errors) {
        showErrorMessage(errors);
        if (!errors.isEmpty()) {
            requestFocusToField(errors.get(0));
        }
    }

    private void showErrorMessage(@NonNull List<IngredientTypeCreateError> errors) {
        List<InputType> inputTypesWithErrors = new ArrayList<>(0);
        for (IngredientTypeCreateError errorType : IngredientTypeCreateError.values()) {
            InputType inputType = errorType.getInputType();
            if (inputTypesWithErrors.contains(inputType)) continue;
            Optional<Integer> errorMessage = searchListFor(errors, errorType);
            if (errorMessage.isPresent()) {
                view.showError(inputType, errorMessage.get());
                inputTypesWithErrors.add(inputType);
            } else {
                view.hideError(inputType);
            }
        }
    }

    private void requestFocusToField(@NonNull IngredientTypeCreateError firstError) {
        view.requestFocusTo(firstError.getInputType());
    }

    @NonNull
    private static Optional<Integer> searchListFor(@NonNull List<IngredientTypeCreateError> errors,
                                                   @NonNull IngredientTypeCreateError error) {
        return errors.contains(error) ? Optional.of(error.getErrorResId()) : Optional.absent();
    }

    private void subscribe(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }
}
