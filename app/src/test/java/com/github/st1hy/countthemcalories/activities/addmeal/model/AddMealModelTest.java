package com.github.st1hy.countthemcalories.activities.addmeal.model;

import android.content.res.Resources;
import android.net.Uri;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDatabaseModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static com.github.st1hy.countthemcalories.activities.addmeal.model.AddMealModel.ParcelableProxy.CREATOR;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.junit.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AddMealModelTest {

    @Mock
    private MealIngredientsListModel listModel;
    @Mock
    private Resources resources;
    @Mock
    private MealDatabaseModel databaseModel;
    private AddMealModel model;

    @Before
    public void setUp() throws Exception {
        model = new AddMealModel(listModel, databaseModel, resources, null);
    }

    @Test
    public void testGetImageSourceDialogTitleRes() throws Exception {
        int titleResId = model.getImageSourceDialogTitleResId();
        assertThat(titleResId, equalTo(R.string.add_meal_image_select_title));
    }

    @Test
    public void testGetImageSourceArrayId() throws Exception {
        int arrayResId = model.getImageSourceOptionArrayResId();
        assertThat(arrayResId, equalTo(R.array.add_meal_image_select_options));
    }


    @Test
    public void testSetName() throws Exception {
        assertThat(model.getName(), equalTo(""));
        model.setName("Name");
        assertThat(model.getName(), equalTo("Name"));
    }

    @Test
    public void testSetImageUri() throws Exception {
        assertThat(model.getImageUri(), equalTo(Uri.EMPTY));
        Uri uri = Mockito.mock(Uri.class);
        model.setImageUri(uri);
        assertThat(model.getImageUri(), equalTo(uri));
    }

    @Test
    public void testProxy() throws Exception {
        assertThat(model.parcelableProxy.describeContents(), equalTo(0));
        assertThat(CREATOR.newArray(4), allOf(instanceOf(AddMealModel.ParcelableProxy[].class), arrayWithSize(4)));
    }
}
