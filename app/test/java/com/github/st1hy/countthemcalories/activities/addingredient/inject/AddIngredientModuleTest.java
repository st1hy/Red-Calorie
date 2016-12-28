package com.github.st1hy.countthemcalories.activities.addingredient.inject;

import android.content.Intent;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
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