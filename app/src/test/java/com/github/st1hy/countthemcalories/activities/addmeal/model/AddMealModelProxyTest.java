package com.github.st1hy.countthemcalories.activities.addmeal.model;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
 @Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class AddMealModelProxyTest {

    final String testName = "Name";
    final Uri testUri = Uri.parse("http://test.org/");

    private MealIngredientsListModel listModel;
    private AddMealModel model;


    @Before
    public void setUp() throws Exception {
        listModel = Mockito.mock(MealIngredientsListModel.class);
        model = new AddMealModel(listModel, null);

        model.setName(testName);
        model.setImageUri(testUri);
    }


    @Test
    public void testOnSaveState() throws Exception {
        Bundle bundle = new Bundle();

        model.onSaveState(bundle);
        AddMealModel restoredModel = new AddMealModel(listModel, bundle);

        assertThat(model.getName(), equalTo(restoredModel.getName()));
        assertThat(model.getImageUri(), equalTo(restoredModel.getImageUri()));
    }

    @Test
    public void testToParcel() throws Exception {
        Parcel parcel = Parcel.obtain();
        model.parcelableProxy.snapshot(model);

        model.parcelableProxy.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);
        AddMealModel.ParcelableProxy fromParcel = AddMealModel.ParcelableProxy.CREATOR.createFromParcel(parcel);

        assertThat(model.getName(), equalTo(fromParcel.name));
        assertThat(model.getImageUri(), equalTo(fromParcel.imageUri));
        parcel.recycle();
    }

    @Test
    public void testSnapshot() throws Exception {
        model.parcelableProxy.snapshot(model);

        assertThat(model.getName(), equalTo(model.parcelableProxy.name));
        assertThat(model.getImageUri(), equalTo(model.parcelableProxy.imageUri));
    }


}
