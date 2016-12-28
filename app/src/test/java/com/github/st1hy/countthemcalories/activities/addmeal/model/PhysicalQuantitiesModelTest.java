package com.github.st1hy.countthemcalories.activities.addmeal.model;

import android.content.Context;

import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.MassUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PhysicalQuantitiesModelTest {

    @Mock
    private SettingsModel settingsModel;
    @Mock
    private Context context;

    private PhysicalQuantitiesModel model;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        model = new PhysicalQuantitiesModel(settingsModel, context);

    }

    @Test
    public void testConvertToPreferred() throws Exception {
        EnergyDensity energyDensity = new EnergyDensity(EnergyUnit.KJ, MassUnit.G,
                new BigDecimal("12.5"));

        when(settingsModel.getAmountUnitFrom(AmountUnitType.MASS)).thenReturn(MassUnit.G100);
        when(settingsModel.getEnergyUnit()).thenReturn(EnergyUnit.KCAL);

        String converted = model.convertAndFormat(energyDensity);

        verify(settingsModel).getAmountUnitFrom(AmountUnitType.MASS);
        verify(settingsModel).getEnergyUnit();

        assertThat(converted, equalTo("298.76 kcal / 100 g"));
        verifyNoMoreInteractions(settingsModel);
    }

    @Test
    public void testFormatEnergyDensity() throws Exception {
        String energyDensity = model.format(
                new EnergyDensity(EnergyUnit.KCAL, MassUnit.G100, new BigDecimal("12.5"))
        );
        assertThat(energyDensity, equalTo("12.5 kcal / 100 g"));
        verifyNoMoreInteractions(settingsModel);
    }

    @Test
    public void testFormatAmount() throws Exception {
        when(settingsModel.getUnitName(VolumeUnit.ML)).thenReturn("ml");

        String amount = model.format(BigDecimal.TEN, VolumeUnit.ML);
        verify(settingsModel).getUnitName(VolumeUnit.ML);

        assertThat(amount, equalTo("10 ml"));
        verifyNoMoreInteractions(settingsModel);
    }

    @Test
    public void testFormatEnergyCount() throws Exception {
        when(settingsModel.getUnitName(EnergyUnit.KCAL)).thenReturn("kcal");

        String calorieCount = model.formatEnergyCount(new BigDecimal("2"), MassUnit.G,
                new EnergyDensity(EnergyUnit.KCAL, MassUnit.G100, new BigDecimal("125")));
        assertThat(calorieCount, equalTo("2.5 kcal"));

        verify(settingsModel).getUnitName(EnergyUnit.KCAL);
        verifyNoMoreInteractions(settingsModel);
    }

    @Test
    public void testConvertAmountFromDatabase() throws Exception {
        BigDecimal bigDecimal = model.convertAmountFromDatabase(new BigDecimal("10"), VolumeUnit.FL_OZ);
        assertThat(bigDecimal.toPlainString(), equalTo("0.33814022701843"));
        verifyNoMoreInteractions(settingsModel);
    }

    @Test
    public void testConvertAmountToDatabase() throws Exception {
        BigDecimal bigDecimal = model.convertAmountToDatabase(new BigDecimal("10"), MassUnit.OZ);
        assertThat(bigDecimal.toPlainString(), equalTo("283.49523125"));
        verifyNoMoreInteractions(settingsModel);
    }

    @Test
    public void testGetSettingsModel() throws Exception {
        assertThat(model.getSettingsModel(), equalTo(settingsModel));
        verifyNoMoreInteractions(settingsModel);
    }

    @Test
    public void testMapToEnergy() throws Exception {
        Ingredient ingredient = mock(Ingredient.class);
        IngredientTemplate template = mock(IngredientTemplate.class);
        when(ingredient.getIngredientType()).thenReturn(template);
        when(template.getAmountType()).thenReturn(AmountUnitType.MASS);
        when(template.getEnergyDensityAmount()).thenReturn(new BigDecimal("20"));
        when(ingredient.getAmount()).thenReturn(new BigDecimal("30"));

        when(settingsModel.getAmountUnitFrom(AmountUnitType.MASS)).thenReturn(MassUnit.OZ);
        when(settingsModel.getEnergyUnit()).thenReturn(EnergyUnit.KCAL);

        BigDecimal energy = model.mapToEnergy().call(ingredient);

        verify(ingredient).getIngredientType();
        verify(template, times(2)).getAmountType();
        verify(template).getEnergyDensityAmount();
        verify(settingsModel).getAmountUnitFrom(AmountUnitType.MASS);
        verify(settingsModel).getEnergyUnit();
        verify(ingredient).getAmount();

        assertThat(energy.toPlainString(), equalTo("143.4"));

        verifyNoMoreInteractions(settingsModel, ingredient, template);
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
        verifyNoMoreInteractions(settingsModel);
    }

    @Test
    public void testSetScale() throws Exception {
        assertThat(model.setScale(2).call(new BigDecimal("43.1063235")).toPlainString(),
                equalTo("43.11"));
        verifyNoMoreInteractions(settingsModel);
    }
}