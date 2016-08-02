package com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter;


import android.support.v4.util.Pair;
import android.view.View;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.MealIngredientsListModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealView;
import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.google.common.base.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;
import java.util.Collections;

import rx.Observable;
import rx.functions.Func1;
import rx.plugins.TestRxPlugins;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class IngredientsAdapterTest {

    @Mock
    private AddMealView view;
    @Mock
    private MealIngredientsListModel model;
    @Mock
    private Picasso picasso;
    @Mock
    private RequestCreator requestCreator;
    @Mock
    private PhysicalQuantitiesModel quantityModel;
    @Mock
    private PermissionsHelper permissionsHelper;

    private IngredientsAdapter adapter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        TestRxPlugins.registerImmediateMainThreadHook();

        when(model.getItemsLoadedObservable()).thenReturn(Observable.<Void>just(null));
        when(model.getExtraIngredient()).thenReturn(Optional.<IngredientTypeParcel>absent());

        Func1<?, BigDecimal> anyDecimal = Functions.into(BigDecimal.TEN);
        when(quantityModel.mapToEnergy()).thenReturn((Func1<Ingredient, BigDecimal>) anyDecimal);
        when(quantityModel.setScale(anyInt())).thenReturn((Func1<BigDecimal, BigDecimal>) anyDecimal);
        when(quantityModel.sumAll()).thenReturn((Func1<BigDecimal, BigDecimal>) anyDecimal);
        Func1<?, String> anyString = Functions.into("energy");
        when(quantityModel.energyAsString()).thenReturn((Func1<BigDecimal, String>) anyString);

        adapter = new IngredientsAdapter(view, model, quantityModel, picasso, permissionsHelper);
    }

    @After
    public void tearDown() throws Exception {
        TestRxPlugins.reset();
    }

    @Test
    public void testOnStart() throws Exception {
        when(model.getItemsCount()).thenReturn(0);
        when(model.getIngredients()).thenReturn(Collections.<Ingredient>emptyList());

        adapter.onStart();

        testVerifyStart();

        verifyNoMoreInteractions(view, model, quantityModel, picasso);
    }

    private void testVerifyStart() {
        verify(model).getItemsLoadedObservable();
        verify(model).getItemsCount();
        verify(model).getIngredients();
        verify(view).setTotalEnergy(anyString());
        verify(quantityModel).mapToEnergy();
        verify(quantityModel).sumAll();
        verify(quantityModel).energyAsString();
        verify(model).getExtraIngredient();
        verify(view).setEmptyIngredientsVisibility(Visibility.VISIBLE);
    }

    @Test
    public void testAddExtraIngredient() throws Exception {
        when(model.getItemsCount()).thenReturn(0);
        when(model.getIngredients()).thenReturn(Collections.<Ingredient>emptyList());
        IngredientTypeParcel parcel = mock(IngredientTypeParcel.class);
        when(model.getExtraIngredient()).thenReturn(Optional.of(parcel));

        adapter.onStart();

        testVerifyStart();

        verify(view).showIngredientDetails(-1L, parcel, BigDecimal.ZERO,
                Collections.<Pair<View,String>>emptyList());
        verifyNoMoreInteractions(view, model, quantityModel, picasso, parcel);
    }
}
