package com.github.st1hy.countthemcalories.activities.addmeal.fragment.model;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;

import com.github.st1hy.countthemcalories.activities.overview.fragment.model.RxMealsDatabaseModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AddMealModelProxyTest {

    private static final String testName = "Name";
    private final Uri testUri = Uri.parse("http://test.org/");

    @Mock
    private MealIngredientsListModel listModel;
    @Mock
    private RxMealsDatabaseModel databaseModel;
    @Mock
    private Resources resources;
    private AddMealModel model;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        model = new AddMealModel(listModel, databaseModel, resources, null, null);

        model.setName(testName);
        model.setImageUri(testUri);
    }

    @Test
    public void testOnSaveState() throws Exception {
        Bundle bundle = new Bundle();

        model.onSaveState(bundle);
        AddMealModel restoredModel = new AddMealModel(listModel, databaseModel, resources, null, bundle);

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
