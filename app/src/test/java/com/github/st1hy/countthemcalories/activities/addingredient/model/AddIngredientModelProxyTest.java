package com.github.st1hy.countthemcalories.activities.addingredient.model;


import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.ParcelableProxy;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesDatabaseModel;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.MassUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumeUnit;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
 @Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class AddIngredientModelProxyTest {

    final EnergyUnit expectedEnergy = EnergyUnit.KCAL;
    final MassUnit expectedMass = MassUnit.G100;
    final VolumeUnit expectedVolume = VolumeUnit.ML100;

    final Uri testUri = Uri.parse("http://test.org/");

    private SettingsModel settingsModel;
    private AddIngredientModel model;
    private IngredientTagsModel tagsModel;
    private IngredientTypesDatabaseModel typesModel;
    private Resources resources = RuntimeEnvironment.application.getResources();


    @Before
    public void setUp() throws Exception {
        settingsModel = Mockito.mock(SettingsModel.class);
        tagsModel = Mockito.mock(IngredientTagsModel.class);
        typesModel = Mockito.mock(IngredientTypesDatabaseModel.class);

        when(settingsModel.getEnergyUnit()).thenReturn(expectedEnergy);
        when(settingsModel.getMassUnit()).thenReturn(expectedMass);
        when(settingsModel.getVolumeUnit()).thenReturn(expectedVolume);
        model = new AddIngredientModel(settingsModel, tagsModel, typesModel, resources, null, null);

        model.setName("testName");
        model.setName("testEnergy");
        model.setImageUri(testUri);
    }

    @Test
    public void testOnSaveState() throws Exception {
        Bundle bundle = new Bundle();

        model.onSaveState(bundle);
        AddIngredientModel restoredModel = new AddIngredientModel(settingsModel, tagsModel, typesModel, resources, bundle, null);

        assertThat(model.amountType, equalTo(restoredModel.amountType));
        assertThat(model.amountUnit, equalTo(restoredModel.amountUnit));
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

        assertThat(model.amountType, equalTo(fromParcel.amountType));
        assertThat(model.getName(), equalTo(fromParcel.name));
        assertThat(model.getEnergyValue(), equalTo(fromParcel.energyValue));
        assertThat(model.getImageUri(), equalTo(fromParcel.imageUri));
        parcel.recycle();
    }

    @Test
    public void testProxyOther() throws Exception {
        MatcherAssert.assertThat(model.parcelableProxy.describeContents(), Matchers.equalTo(0));
        assertThat(AddIngredientModel.ParcelableProxy.CREATOR.newArray(4),
                allOf(instanceOf(AddIngredientModel.ParcelableProxy[].class), arrayWithSize(4)));
    }
}
