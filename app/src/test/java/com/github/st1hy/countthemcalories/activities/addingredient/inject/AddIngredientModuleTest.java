package com.github.st1hy.countthemcalories.activities.addingredient.inject;

import android.content.Intent;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddIngredientModuleTest {

    @Mock
    private Intent intent;

    @Test
    public void testDefaultUnitType() throws Exception {
        assertThat(AddIngredientModule.provideUnitType(intent), equalTo(AmountUnitType.MASS));
    }


    @Test
    public void testMealUnitType() throws Exception {
        when(intent.getAction()).thenReturn(AddIngredientType.MEAL.getAction());
        assertThat(AddIngredientModule.provideUnitType(intent), equalTo(AmountUnitType.MASS));

    }

    @Test
    public void testDrinkUnitType() throws Exception {
        when(intent.getAction()).thenReturn(AddIngredientType.DRINK.getAction());
        assertThat(AddIngredientModule.provideUnitType(intent), equalTo(AmountUnitType.VOLUME));
    }
}