package com.github.st1hy.countthemcalories.activities.addingredient.model;

import android.content.res.Resources;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesDatabaseModel;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.MassUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateError.NO_NAME;
import static com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateError.NO_VALUE;
import static com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateError.ZERO_VALUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddIngredientModelTest {

    final EnergyUnit expectedEnergy = EnergyUnit.KCAL;
    final MassUnit expectedMass = MassUnit.G100;
    final VolumeUnit expectedVolume = VolumeUnit.ML100;

    @Mock
    private SettingsModel settingsModel;
    @Mock
    private IngredientTagsModel tagsModel;
    @Mock
    private IngredientTypesDatabaseModel typesModel;
    @Mock
    private Resources resources;
    private AddIngredientModel model;

    @Before
    public void setUp() throws Exception {
        when(settingsModel.getEnergyUnit()).thenReturn(expectedEnergy);
        when(settingsModel.getMassUnit()).thenReturn(expectedMass);
        when(settingsModel.getVolumeUnit()).thenReturn(expectedVolume);
        model = new AddIngredientModel(settingsModel, tagsModel, typesModel, resources, null, null);
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
        model.energyValue = "0";
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