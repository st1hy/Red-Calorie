package com.github.st1hy.countthemcalories.activities.addingredient.model;

import android.database.Cursor;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesModel;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Tag;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.List;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AddIngredientModelRoboTest {

    private IngredientTagsModel tagsModel;
    private IngredientTypesModel typesModel;
    private AddIngredientModel model;

    @Before
    public void setUp() throws Exception {
        SettingsModel settingsModel = Mockito.mock(SettingsModel.class);
        tagsModel = Mockito.mock(IngredientTagsModel.class);
        typesModel = Mockito.mock(IngredientTypesModel.class);
        model = new AddIngredientModel(settingsModel, tagsModel, typesModel, null);
    }

    //JodaTime complains of missing timezones without Robolectric
    @Test
    public void testInsertToDatabase() throws Exception {
        model.name = "Test";
        model.energyValue = "100";
        Tag example = new Tag(null, "Tag");
        final List<Tag> tags = Collections.singletonList(example);
        Cursor cursor = Mockito.mock(Cursor.class);

        when(tagsModel.getAll()).thenReturn(tags);
        when(typesModel.addNewAndRefresh(any(IngredientTemplate.class), eq(tags)))
                .thenReturn(Observable.just(cursor));

        model.insertIntoDatabase();

        verify(tagsModel).getAll();
        verify(typesModel).addNewAndRefresh(any(IngredientTemplate.class), eq(tags));
    }

}
