package com.github.st1hy.countthemcalories.activities.addmeal.model;

import android.net.Uri;

import com.github.st1hy.countthemcalories.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AddMealModelTest {

    @Mock
    private MealIngredientsListModel listModel;
    private AddMealModel model;

    @Before
    public void setUp() throws Exception {
        model = new AddMealModel(listModel, null);
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
}
