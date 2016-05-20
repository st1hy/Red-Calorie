package com.github.st1hy.countthemcalories.activities.addmeal.model;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.MassUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumeUnit;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

@RunWith(RobolectricGradleTestRunner.class)
 @Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class PhysicalQuantitiesModelTest {

    private PhysicalQuantitiesModel model;

    @Before
    public void setUp() throws Exception {
        CaloriesCounterApplication application = (CaloriesCounterApplication) RuntimeEnvironment.application;
        model = new PhysicalQuantitiesModel(application.getComponent().getSettingsModel(), application.getResources());
    }

    @Test
    public void testFormatEnergyDensity() throws Exception {
        String energyDensity = model.format(
                new EnergyDensity(EnergyUnit.KCAL, MassUnit.G100, new BigDecimal("12.5"))
        );
        assertThat(energyDensity, equalTo("12.5 kcal / 100 g"));
    }

    @Test
    public void testFormatAmount() throws Exception {
        String amount = model.format(BigDecimal.TEN, VolumeUnit.ML);
        assertThat(amount, equalTo("10 ml"));
    }

    @Test
    public void testFormatEnergyCount() throws Exception {
        String calorieCount = model.formatEnergyCount(new BigDecimal("2"), MassUnit.G,
                new EnergyDensity(EnergyUnit.KCAL, MassUnit.G100, new BigDecimal("125")));
        assertThat(calorieCount, equalTo("2.5 kcal"));
    }

    @Test
    public void testSumAll() throws Exception {
        Observable.range(1, 5)
                .map(new Func1<Integer, BigDecimal>() {
                    @Override
                    public BigDecimal call(Integer integer) {
                        return new BigDecimal(integer);
                    }
                })
                .map(model.sumAll())
                .toList().subscribe(new Action1<List<BigDecimal>>() {
            @Override
            public void call(List<BigDecimal> bigDecimals) {
                assertThat(bigDecimals, hasItems(of(1), of(3), of(6), of(10), of(15)));
            }

            BigDecimal of(int i) {
                return new BigDecimal(i);
            }
        });

    }
}