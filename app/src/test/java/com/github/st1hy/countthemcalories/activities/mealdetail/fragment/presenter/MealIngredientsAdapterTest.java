package com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.model.MealDetailModel;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.viewholder.IngredientViewHolder;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.MassUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.Observable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MealIngredientsAdapterTest {

    private MealIngredientsAdapter adapter;
    @Mock
    MealDetailModel model;
    @Mock
    PhysicalQuantitiesModel quantitiesModel;
    @Mock
    ViewGroup parent;
    @Mock
    Context context;

    @Before
    public void setUp() throws Exception {
        when(parent.getContext()).thenReturn(context);
        when(parent.generateLayoutParams(any(AttributeSet.class)))
                .thenReturn(new ViewGroup.LayoutParams(100, 100));
        adapter = new MealIngredientsAdapter(model, quantitiesModel);
    }

    @Test
    public void testOnStart() throws Exception {
        Meal meal = mock(Meal.class);
        when(model.getMealObservable()).thenReturn(Observable.just(meal));
        List<Ingredient> ingredients = Collections.emptyList();
        when(meal.getIngredients()).thenReturn(ingredients);

        adapter.onStart();

        verify(model).getMealObservable();
        verify(meal).getIngredients();
        assertThat(adapter.ingredients, equalTo(ingredients));

        verifyNoMoreInteractions(model, quantitiesModel, parent, meal);
    }

    @Test
    public void testOnStop() throws Exception {
        testOnStart();
        adapter.onStop();

        assertThat(adapter.subscriptions.isUnsubscribed(), equalTo(false));
        assertThat(adapter.subscriptions.hasSubscriptions(), equalTo(false));

        verifyNoMoreInteractions(model, quantitiesModel, parent);
    }

    @Test
    public void testOnCreateViewHolder() throws Exception {
        IngredientViewHolder ingredientViewHolder = adapter.onCreateViewHolder(parent, 0);

        assertThat(ingredientViewHolder, notNullValue());
        verify(parent).getContext();
        verify(parent).generateLayoutParams(any(AttributeSet.class));

        verifyNoMoreInteractions(model, quantitiesModel, parent);
    }

    @Test
    public void testOnBindViewHolder() throws Exception {
        IngredientViewHolder holder = mock(IngredientViewHolder.class);
        Ingredient ingredient = mock(Ingredient.class);
        IngredientTemplate template = mock(IngredientTemplate.class);
        when(ingredient.getIngredientType()).thenReturn(template);
        when(template.getName()).thenReturn("Ingredient name");
        adapter.ingredients = new ArrayList<>();
        adapter.ingredients.add(ingredient);
        assertThat(adapter.ingredients.get(0), equalTo(ingredient));
        when(template.getAmountType()).thenReturn(AmountUnitType.MASS);
        EnergyDensity energyDensity = EnergyDensityUtils.getOrZero(EnergyUnit.KCAL, MassUnit.G100, "200");
        when(quantitiesModel.convertToPreferred(any(EnergyDensity.class)))
                .thenReturn(energyDensity);
        when(quantitiesModel.formatEnergyCount(any(BigDecimal.class), eq(MassUnit.G), eq(energyDensity)))
                .thenReturn("energy amount");
        BigDecimal amount = new BigDecimal("341");
        when(quantitiesModel.convertAmountFromDatabase(any(BigDecimal.class), eq(MassUnit.G)))
                .thenReturn(amount);
        when(quantitiesModel.format(amount, MassUnit.G)).thenReturn("amount");

        adapter.onBindViewHolder(holder, 0);

        verify(holder).setName("Ingredient name");
        verify(holder).setEnergy("energy amount");
        verify(holder).setAmount("amount");

        verify(quantitiesModel).convertToPreferred(any(EnergyDensity.class));
        verify(quantitiesModel).formatEnergyCount(any(BigDecimal.class), eq(MassUnit.G), eq(energyDensity));
        verify(quantitiesModel).convertAmountFromDatabase(any(BigDecimal.class), eq(MassUnit.G));
        verify(quantitiesModel).format(amount, MassUnit.G);

        verify(ingredient).getIngredientType();
        verify(ingredient).getAmount();

        verify(template).getName();
        verify(template).getEnergyDensityAmount();
        verify(template).getAmountType();

        verifyNoMoreInteractions(model, quantitiesModel, parent, holder, ingredient, template);
    }

    @Test
    public void testGetItemCount() throws Exception {
        Ingredient[] ingredients = new Ingredient[5];
        Arrays.fill(ingredients, mock(Ingredient.class));
        adapter.ingredients = Arrays.asList(ingredients);

        int itemCount = adapter.getItemCount();
        assertThat(itemCount, equalTo(5));

        verifyNoMoreInteractions(model, quantitiesModel, parent);
    }
}