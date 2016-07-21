package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.model.MealDetailModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailView;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;
import java.util.Collections;

import rx.Observable;
import rx.functions.Func1;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static rx.Observable.just;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class MealDetailPresenterImplTest {

    MealDetailPresenterImpl presenter;

    @Mock
    PhysicalQuantitiesModel quantityModel;
    @Mock
    MealDetailView view;
    @Mock
    MealDetailModel model;
    @Mock
    Picasso picasso;
    @Mock
    MealIngredientsAdapter adapter;
    @Mock
    RequestCreator requestCreator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(picasso.load(any(Uri.class))).thenReturn(requestCreator);
        presenter = new MealDetailPresenterImpl(view, model, picasso, adapter, quantityModel);
    }

    @Test
    public void testOnStart() throws Exception {
        when(view.getDeleteObservable()).thenReturn(Observable.<Void>empty());
        when(view.getEditObservable()).thenReturn(Observable.<Void>empty());
        when(model.getMealObservable()).thenReturn(Observable.<Meal>empty());

        presenter.onStart();
        verifyOnStartValid();

        verifyNoMoreInteractions(quantityModel, view, model, picasso, adapter);
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
        when(view.getImageView()).thenReturn(mock(ImageView.class));

        presenter.onStart();
        verifyOnStartValid();
        verify(view).setDate(anyString());
        verify(view).setName(anyString());
        verify(view).setEnergy("energy");
        verify(quantityModel).mapToEnergy();
        verify(quantityModel).sumAll();
        verify(quantityModel).energyAsString();
        verify(quantityModel).formatTime(any(DateTime.class));
        verify(view).getImageView();
        verify(picasso).load(uri);

        verifyNoMoreInteractions(quantityModel, view, model, picasso, adapter);
    }

    private void verifyOnStartValid() {
        verify(model).getMealObservable();
        verify(view).getDeleteObservable();
        verify(view).getEditObservable();
        verify(adapter).onStart();
    }

    @Test
    public void testOnStop() throws Exception {
        testOnStart();
        presenter.onStop();

        assertThat(presenter.subscriptions.isUnsubscribed(), equalTo(false));
        assertThat(presenter.subscriptions.hasSubscriptions(), equalTo(false));

        verify(adapter).onStop();

        verifyNoMoreInteractions(quantityModel, view, model, picasso, adapter);
    }

    @Test
    public void testOnSaveState() throws Exception {
        Bundle bundle = mock(Bundle.class);
        presenter.onSaveState(bundle);
        verify(model).onSaveState(bundle);

        verifyNoMoreInteractions(quantityModel, view, model, picasso, adapter);
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

        verifyNoMoreInteractions(quantityModel, view, model, picasso, adapter, meal);

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

        verifyNoMoreInteractions(quantityModel, view, model, picasso, adapter, meal);
    }

    @Test
    public void testBindImageNoUri() throws Exception {
        Meal meal = mock(Meal.class);
        when(meal.getImageUri()).thenReturn(Uri.EMPTY);
        ImageView imageView = mock(ImageView.class);
        when(view.getImageView()).thenReturn(imageView);

        presenter.bindImage(meal);

        verify(view).getImageView();
        verify(meal).getImageUri();
        verify(imageView).setImageResource(R.drawable.ic_fork_and_knife_wide);

        verifyNoMoreInteractions(quantityModel, view, model, picasso, adapter, meal, imageView);
    }
}