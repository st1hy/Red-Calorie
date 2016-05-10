package com.github.st1hy.countthemcalories.activities.addingredient.model;


import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.ParcelableProxy;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesDatabaseModel;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit.KCAL_AT_100G;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KCAL_AT_ML;
import static com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit.KJ_AT_100ML;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AddIngredientModelProxyTest {

    final GravimetricEnergyDensityUnit expectedDefault = KCAL_AT_100G;
    final VolumetricEnergyDensityUnit expectedDefaultLiquid = KJ_AT_100ML;
    final Uri testUri = Uri.parse("http://test.org/");

    private SettingsModel settingsModel;
    private AddIngredientModel model;
    private IngredientTagsModel tagsModel;
    private IngredientTypesDatabaseModel typesModel;


    @Before
    public void setUp() throws Exception {
        settingsModel = Mockito.mock(SettingsModel.class);
        tagsModel = Mockito.mock(IngredientTagsModel.class);
        typesModel = Mockito.mock(IngredientTypesDatabaseModel.class);
        when(settingsModel.getPreferredGravimetricUnit()).thenReturn(expectedDefault);
        when(settingsModel.getPreferredVolumetricUnit()).thenReturn(expectedDefaultLiquid);
        model = new AddIngredientModel(settingsModel, tagsModel, typesModel, null);

        model.setUnit(KCAL_AT_ML);
        model.setName("testName");
        model.setName("testEnergy");
        model.setImageUri(testUri);
    }

    @Test
    public void testOnSaveState() throws Exception {
        Bundle bundle = new Bundle();

        model.onSaveState(bundle);
        AddIngredientModel restoredModel = new AddIngredientModel(settingsModel, tagsModel, typesModel, bundle);

        assertThat(model.unit.getValue(), equalTo(restoredModel.unit.getValue()));
        assertThat(model.getName(), equalTo(restoredModel.getName()));
        assertThat(model.getEnergyValue(), equalTo(restoredModel.getEnergyValue()));
        assertThat(model.getImageUri(), equalTo(restoredModel.getImageUri()));
    }

    @Test
    public void testToParcel() throws Exception {
        Parcel parcel = Parcel.obtain();
        model.parcelableProxy.snapshot(model);

        model.parcelableProxy.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);
        ParcelableProxy fromParcel = ParcelableProxy.CREATOR.createFromParcel(parcel);

        assertThat(model.unit.getValue(), equalTo(fromParcel.unit));
        assertThat(model.getName(), equalTo(fromParcel.name));
        assertThat(model.getEnergyValue(), equalTo(fromParcel.energyValue));
        assertThat(model.getImageUri(), equalTo(fromParcel.imageUri));
        parcel.recycle();
    }

    @Test
    public void testSnapshot() throws Exception {
        model.parcelableProxy.snapshot(model);

        assertThat(model.unit.getValue(), equalTo(model.parcelableProxy.unit));
        assertThat(model.getName(), equalTo(model.parcelableProxy.name));
        assertThat(model.getEnergyValue(), equalTo(model.parcelableProxy.energyValue));
        assertThat(model.getImageUri(), equalTo(model.parcelableProxy.imageUri));
    }
}
