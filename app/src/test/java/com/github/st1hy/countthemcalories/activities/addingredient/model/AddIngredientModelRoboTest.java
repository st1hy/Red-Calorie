package com.github.st1hy.countthemcalories.activities.addingredient.model;

import android.content.res.Resources;
import android.database.Cursor;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.addingredient.model.AddIngredientModel.IngredientTypeCreateError;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesDatabaseModel;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
 @Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class AddIngredientModelRoboTest {

    private IngredientTagsModel tagsModel;
    private IngredientTypesDatabaseModel typesModel;
    private AddIngredientModel model;

    @Before
    public void setUp() throws Exception {
        CaloriesCounterApplication application = (CaloriesCounterApplication) RuntimeEnvironment.application;
        SettingsModel settingsModel = application.getComponent().getSettingsModel();
        tagsModel = Mockito.mock(IngredientTagsModel.class);
        typesModel = Mockito.mock(IngredientTypesDatabaseModel.class);
        Resources resources = application.getResources();
        model = new AddIngredientModel(settingsModel, tagsModel, typesModel, resources, null, null);
    }

    //JodaTime complains of missing timezones without Robolectric
    @Test
    public void testInsertToDatabase() throws Exception {
        model.name = "Test";
        model.energyValue = "100";
        final List<Long> tagIds = Collections.singletonList(33L);
        Cursor cursor = Mockito.mock(Cursor.class);

        when(tagsModel.getTagIds()).thenReturn(tagIds);
        doReturn(Observable.just(cursor)).when(typesModel).addNew(any(IngredientTemplate.class), eq(tagIds));

        model.insertIntoDatabase();

        verify(tagsModel).getTagIds();
        verify(typesModel).addNew(any(IngredientTemplate.class), eq(tagIds));
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
