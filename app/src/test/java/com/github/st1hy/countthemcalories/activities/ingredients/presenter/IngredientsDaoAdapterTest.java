package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientsActivityModel;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsView;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.squareup.picasso.Picasso;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class IngredientsDaoAdapterTest {

    private IngredientsView view;
    private IngredientsActivityModel activityModel;
    private IngredientTypesModel model;
    private SettingsModel settingsModel;
    private Picasso picasso;
    private IngredientsDaoAdapter adapter;

    @Before
    public void setup() {
        view = Mockito.mock(IngredientsView.class);
        activityModel= Mockito.mock(IngredientsActivityModel.class);
        model = Mockito.mock(IngredientTypesModel.class);
        settingsModel = Mockito.mock(SettingsModel.class);
        picasso = Mockito.mock(Picasso.class);
        adapter = new IngredientsDaoAdapter(view, activityModel, model, settingsModel, picasso);
    }

    @Test
    public void testStart() throws Exception {
        adapter.onStart();

        verifyNoMoreInteractions(view, activityModel, model, settingsModel, picasso);
    }
}