package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.model.MealDetailModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailView;
import com.github.st1hy.countthemcalories.core.picture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.testutils.OptionalMatchers;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;

import rx.Observable;
import rx.functions.Func1;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static rx.Observable.just;

@RunWith(MockitoJUnitRunner.class)
public class MealDetailPresenterImplTest {

    MealDetailPresenterImpl presenter;

    @Mock
    PhysicalQuantitiesModel quantityModel;
    @Mock
    MealDetailView view;
    @Mock
    MealDetailModel model;
    @Mock
    MealIngredientsAdapter adapter;
    @Mock
    ImageHolderDelegate imageHolderDelegate;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new MealDetailPresenterImpl(view, model, adapter, quantityModel,
                imageHolderDelegate);
    }

    @Test
    public void testOnStart() throws Exception {
        when(view.getDeleteObservable()).thenReturn(Observable.<Void>empty());
        when(view.getEditObservable()).thenReturn(Observable.<Void>empty());
        when(model.getMealObservable()).thenReturn(Observable.<Meal>empty());

        presenter.onStart();
        verifyOnStartValid();

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testMealBinding() throws Exception {
        when(view.getDeleteObservable()).thenReturn(Observable.<Void>empty());
        when(view.getEditObservable()).thenReturn(Observable.<Void>empty());
        Meal meal = mock(Meal.class);
        Uri uri = mock(Uri.class);
        when(meal.getImageUri()).thenReturn(uri);
        when(model.getMealObservable()).thenReturn(just(meal));
        when(quantityModel.energyAsString()).thenReturn(new Func1<BigDecimal, String>() {
            @Override
            public String call(BigDecimal decimal) {
                return "energy";
            }
        });
        when(meal.getIngredients()).thenReturn(Collections.<Ingredient>emptyList());

        presenter.onStart();
        verifyOnStartValid();
        verify(view).setDate(anyString());
        verify(view).setName(anyString());
        verify(view).setEnergy("energy");
        verify(quantityModel).mapToEnergy();
        verify(quantityModel).sumAll();
        verify(quantityModel).energyAsString();
        verify(quantityModel).formatTime(any(DateTime.class));
        verify(imageHolderDelegate).displayImage(argThat(OptionalMatchers.equalTo(uri)));

        testVerifyNoMoreInteraction();
    }

    private void verifyOnStartValid() {
        verify(model).getMealObservable();
        verify(view).getDeleteObservable();
        verify(view).getEditObservable();
        verify(adapter).onStart();
        verify(imageHolderDelegate).onAttached();
    }

    @Test
    public void testOnStop() throws Exception {
        testOnStart();
        presenter.onStop();

        assertThat(presenter.subscriptions.isUnsubscribed(), equalTo(false));
        assertThat(presenter.subscriptions.hasSubscriptions(), equalTo(false));

        verify(adapter).onStop();
        verify(imageHolderDelegate).onDetached();

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testOnSaveState() throws Exception {
        Bundle bundle = mock(Bundle.class);
        presenter.onSaveState(bundle);
        verify(model).onSaveState(bundle);

        testVerifyNoMoreInteraction();
    }

    @Test
    public void testEdit() throws Exception {
        when(view.getEditObservable()).thenReturn(Observable.<Void>just(null));
        when(view.getDeleteObservable()).thenReturn(Observable.<Void>empty());
        when(model.getMealObservable()).thenReturn(Observable.<Meal>empty());
        Meal meal = mock(Meal.class);
        when(model.getMeal()).thenReturn(meal);
        when(meal.getId()).thenReturn(2016L);

        presenter.onStart();
        verifyOnStartValid();
        verify(model).getMeal();
        verify(meal).getId();
        verify(view).editMealWithId(2016L);

        testVerifyNoMoreInteraction();
        verifyNoMoreInteractions(meal);

    }

    @Test
    public void testRemove() throws Exception {
        when(view.getDeleteObservable()).thenReturn(Observable.<Void>just(null));
        when(view.getEditObservable()).thenReturn(Observable.<Void>empty());
        when(model.getMealObservable()).thenReturn(Observable.<Meal>empty());
        Meal meal = mock(Meal.class);
        when(model.getMeal()).thenReturn(meal);
        when(meal.getId()).thenReturn(2016L);

        presenter.onStart();
        verifyOnStartValid();
        verify(model).getMeal();
        verify(meal).getId();
        verify(view).deleteMealWithId(2016L);

        testVerifyNoMoreInteraction();
        verifyNoMoreInteractions(meal);
    }

    @Test
    public void testBindImageNoUri() throws Exception {
        Meal meal = mock(Meal.class);
        when(meal.getImageUri()).thenReturn(Uri.EMPTY);
        ImageView imageView = mock(ImageView.class);

        presenter.bindImage(meal);

        verify(meal).getImageUri();
        verify(imageHolderDelegate).displayImage(argThat(OptionalMatchers.<Uri>isAbsent()));

        testVerifyNoMoreInteraction();
        verifyNoMoreInteractions(meal, imageView);
    }

    private void testVerifyNoMoreInteraction() {
        verifyNoMoreInteractions(quantityModel, view, model, adapter, imageHolderDelegate);
    }
}