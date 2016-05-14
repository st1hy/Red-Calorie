package com.github.st1hy.countthemcalories.activities.addmeal.model;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;
import java.util.Locale;

import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KCAL_AT_100G;
import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KJ_AT_G;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KCAL_AT_ML;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class UnitNamesModelTest {

    private UnitNamesModel model;

    @Before
    public void setUp() throws Exception {
        CaloriesCounterApplication application = (CaloriesCounterApplication) RuntimeEnvironment.application;
        model = new UnitNamesModel(application.getComponent().getSettingsModel(), application.getResources());
    }

    @Test
    public void testGetReadableEnergyDensity() throws Exception {
        String energyDensity = model.getReadableEnergyDensity(
                EnergyDensityUtils.getOrZero(KCAL_AT_100G, "12.5"));
        assertThat(energyDensity, equalTo("12.5 kcal / 100 g"));
    }

    @Test
    public void testGetReadableAmount() throws Exception {
        String amount = model.getReadableAmount(BigDecimal.TEN, VolumetricEnergyDensityUnit.KJ_AT_100ML);
        assertThat(amount, equalTo("10 ml"));
    }

    @Test
    public void testGetUnitFormat() throws Exception {
        assertThat(model.getUnitFormat(KCAL_AT_ML), equalTo(R.string.format_milliliter));
        assertThat(model.getUnitFormat(KJ_AT_G), equalTo(R.string.format_gram));
    }

    @Test
    public void testGetCalorieCount() throws Exception {
        String calorieCount = model.getCalorieCount(new BigDecimal("2"),
                EnergyDensityUtils.getOrZero(VolumetricEnergyDensityUnit.KJ_AT_ML, "10"));
        double expected = 2 * 10 / EnergyUnit.KCAL.getBase().doubleValue();
        assertThat(calorieCount, equalTo(String.format(Locale.ENGLISH, "%.2f kcal", expected)));
    }

    @Test
    public void testGetUnitFormat1() throws Exception {
        assertThat(model.getUnitFormat(EnergyUnit.KCAL), equalTo(R.string.format_kcal));
        assertThat(model.getUnitFormat(EnergyUnit.KJ), equalTo(R.string.format_kj));
    }

    @Test
    public void testGetUnitName() throws Exception {
        assertThat(model.getUnitName(KCAL_AT_ML), equalTo(R.string.unit_milliliter));
        assertThat(model.getUnitName(KJ_AT_G), equalTo(R.string.unit_gram));
    }
}