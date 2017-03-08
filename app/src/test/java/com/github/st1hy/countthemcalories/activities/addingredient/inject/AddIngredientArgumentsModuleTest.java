package com.github.st1hy.countthemcalories.activities.addingredient.inject;

import android.content.Intent;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.AddIngredientArgumentsModule.provideUnitType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddIngredientArgumentsModuleTest {

    @Mock
    private Intent intent;

    @Test
    public void testDefaultUnitType() throws Exception {
        assertThat(provideUnitType(intent), equalTo(AmountUnitType.MASS));
    }


    @Test
    public void testMealUnitType() throws Exception {
        when(intent.getAction()).thenReturn(AddIngredientType.MEAL.getAction());
        assertThat(provideUnitType(intent), equalTo(AmountUnitType.MASS));

    }

    @Test
    public void testDrinkUnitType() throws Exception {
        when(intent.getAction()).thenReturn(AddIngredientType.DRINK.getAction());
        assertThat(provideUnitType(intent), equalTo(AmountUnitType.VOLUME));
    }
}