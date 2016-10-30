package com.github.st1hy.countthemcalories.activities.addingredient.inject;

import android.content.Intent;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(MockitoJUnitRunner.class)
public class AddIngredientModuleTest {

    AddIngredientModule module;

    @Before
    public void setUp() throws Exception {
        module = new AddIngredientModule(null);
    }

    @Test
    public void testGetUnitType() throws Exception {
        Intent intent = new Intent();
        assertThat(module.provideUnitType(intent), equalTo(AmountUnitType.MASS));
        intent.setAction(AddIngredientType.MEAL.getAction());
        assertThat(module.provideUnitType(intent), equalTo(AmountUnitType.MASS));
        intent.setAction(AddIngredientType.DRINK.getAction());
        assertThat(module.provideUnitType(intent), equalTo(AmountUnitType.VOLUME));
    }

}