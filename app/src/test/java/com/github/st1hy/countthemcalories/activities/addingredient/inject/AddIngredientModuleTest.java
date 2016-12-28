package com.github.st1hy.countthemcalories.activities.addingredient.inject;

import android.content.Intent;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.inject.activities.addingredient.AddIngredientModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddIngredientModuleTest {

    AddIngredientModule module;

    @Mock
    private Intent intent;

    @Before
    public void setUp() throws Exception {
        module = new AddIngredientModule(null);
    }

    @Test
    public void testDefaultUnitType() throws Exception {
        assertThat(module.provideUnitType(intent), equalTo(AmountUnitType.MASS));
    }


    @Test
    public void testMealUnitType() throws Exception {
        when(intent.getAction()).thenReturn(AddIngredientType.MEAL.getAction());
        assertThat(module.provideUnitType(intent), equalTo(AmountUnitType.MASS));

    }

    @Test
    public void testDrinkUnitType() throws Exception {
        when(intent.getAction()).thenReturn(AddIngredientType.DRINK.getAction());
        assertThat(module.provideUnitType(intent), equalTo(AmountUnitType.VOLUME));
    }
}