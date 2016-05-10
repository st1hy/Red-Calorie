package com.github.st1hy.countthemcalories.activities.addingredient.model;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesDatabaseModel;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import rx.functions.Action1;

import static com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateError.NO_NAME;
import static com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateError.NO_VALUE;
import static com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateError.ZERO_VALUE;
import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KCAL_AT_100G;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KJ_AT_100ML;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddIngredientModelTest {

    final GravimetricEnergyDensityUnit expectedDefault = KCAL_AT_100G;
    final VolumetricEnergyDensityUnit expectedDefaultLiquid = KJ_AT_100ML;

    @Mock
    private SettingsModel settingsModel;
    @Mock
    private IngredientTagsModel tagsModel;
    @Mock
    private IngredientTypesDatabaseModel typesModel;
    private AddIngredientModel model;

    @Before
    public void setUp() throws Exception {
        when(settingsModel.getPreferredGravimetricUnit()).thenReturn(expectedDefault);
        when(settingsModel.getPreferredVolumetricUnit()).thenReturn(expectedDefaultLiquid);
        model = new AddIngredientModel(settingsModel, tagsModel, typesModel, null);
    }

    @Test
    public void testGetUnitSelection() throws Exception {
        EnergyDensityUnit[] unitSelection = model.getUnitSelection();
        assertThat(unitSelection, Matchers.<EnergyDensityUnit>arrayContaining(expectedDefault, expectedDefaultLiquid));
    }

    @Test
    public void testGetUnitSelectionOptions() throws Exception {
        when(settingsModel.getUnitName(expectedDefault)).thenReturn("1");
        when(settingsModel.getUnitName(expectedDefaultLiquid)).thenReturn("2");
        String[] options = model.getUnitSelectionOptions();
        assertThat(options, arrayContaining("1", "2"));

    }

    @Test
    public void testSetUnit() throws Exception {
        final AtomicInteger callCount = new AtomicInteger(0);
        EnergyDensityUnit expectedValue = expectedDefault;
        final AtomicReference<EnergyDensityUnit> expected = new AtomicReference<>(expectedValue);
        model.getUnitObservable().subscribe(new Action1<EnergyDensityUnit>() {
            @Override
            public void call(EnergyDensityUnit energyDensityUnit) {
                callCount.incrementAndGet();
                assertThat(expected.get(), equalTo(energyDensityUnit));
            }
        });
        expectedValue = VolumetricEnergyDensityUnit.KCAL_AT_ML;
        expected.set(expectedValue);
        model.setUnit(expected.get());
        assertEquals(2, callCount.get());
    }

    @Test
    public void testUnitAsString() throws Exception {
        VolumetricEnergyDensityUnit expected = VolumetricEnergyDensityUnit.KJ_AT_100ML;
        model.unitAsString().call(expected);
        verify(settingsModel).getUnitName(expected);
    }

    @Test
    public void testGetUnitDialogTitle() throws Exception {
        int title = model.getSelectUnitDialogTitle();
        assertEquals(R.string.add_ingredient_select_unit_dialog_title, title);
    }

    @Test
    public void testGeImageSourceDialogTitle() throws Exception {
        int title = model.getImageSourceDialogTitleResId();
        assertEquals(R.string.add_ingredient_image_select_title, title);
    }

    @Test
    public void testGeImageSourceOptions() throws Exception {
        int array = model.getImageSourceOptionArrayResId();
        assertEquals(R.array.add_ingredient_image_select_options, array);
    }

    @Test
    public void testCanCreateIngredient() throws Exception {
        model.name = "";
        model.energyValue = "";
        assertThat(model.canCreateIngredient(), hasItems(NO_NAME, NO_VALUE));
        model.name = "s";
        model.energyValue = "";
        assertThat(model.canCreateIngredient(), hasItems(NO_VALUE));
        model.name = "";
        model.energyValue = "0";;
        assertThat(model.canCreateIngredient(), hasItems(NO_NAME, ZERO_VALUE));
        model.name = "";
        model.energyValue = "1";
        assertThat(model.canCreateIngredient(), hasItems(NO_NAME));
        model.name = "s";
        model.energyValue = "1s";
        assertThat(model.canCreateIngredient(), hasItems(ZERO_VALUE));
        model.name = "s";
        model.energyValue = "0.0000";
        assertThat(model.canCreateIngredient(), hasItems(ZERO_VALUE));
        model.name = "s";
        model.energyValue = "100";
        assertThat(model.canCreateIngredient(), hasSize(0));
    }

}