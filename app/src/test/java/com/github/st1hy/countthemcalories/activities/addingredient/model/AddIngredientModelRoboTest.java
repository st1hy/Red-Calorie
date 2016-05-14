package com.github.st1hy.countthemcalories.activities.addingredient.model;

import android.database.Cursor;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateError;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesDatabaseModel;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.functions.Action1;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AddIngredientModelRoboTest {

    private IngredientTagsModel tagsModel;
    private IngredientTypesDatabaseModel typesModel;
    private AddIngredientModel model;

    @Before
    public void setUp() throws Exception {
        SettingsModel settingsModel = Mockito.mock(SettingsModel.class);
        tagsModel = Mockito.mock(IngredientTagsModel.class);
        typesModel = Mockito.mock(IngredientTypesDatabaseModel.class);
        model = new AddIngredientModel(settingsModel, tagsModel, typesModel, null);
    }

    //JodaTime complains of missing timezones without Robolectric
    @Test
    public void testInsertToDatabase() throws Exception {
        model.name = "Test";
        model.energyValue = "100";
        final List<Long> tagIds = Collections.singletonList(33L);
        Cursor cursor = Mockito.mock(Cursor.class);

        when(tagsModel.getTagIds()).thenReturn(tagIds);
        when(typesModel.addNewAndRefresh(any(IngredientTemplate.class), eq(tagIds)))
                .thenReturn(Observable.just(cursor));

        model.insertIntoDatabase();

        verify(tagsModel).getTagIds();
        verify(typesModel).addNewAndRefresh(any(IngredientTemplate.class), eq(tagIds));
    }


    //JodaTime complains of missing timezones without Robolectric
    @Test
    public void testInsertToDatabaseError() throws Exception {
        model.name = "";
        model.energyValue = "0";

        final AtomicReference<List<IngredientTypeCreateError>> errorsRef = new AtomicReference<>();
        model.insertIntoDatabase().subscribe(new Action1<List<IngredientTypeCreateError>>() {
            @Override
            public void call(List<IngredientTypeCreateError> errors) {
                errorsRef.set(errors);
            }
        });

        verifyZeroInteractions(typesModel, tagsModel);
        assertThat(errorsRef.get(), hasItems(IngredientTypeCreateError.ZERO_VALUE,
                IngredientTypeCreateError.NO_NAME));
    }

}
