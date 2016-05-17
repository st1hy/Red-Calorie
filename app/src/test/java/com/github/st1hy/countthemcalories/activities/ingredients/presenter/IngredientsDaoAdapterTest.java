package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesDatabaseModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientsModel;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsView;
import com.github.st1hy.countthemcalories.testutils.RobolectricConfig;
import com.squareup.picasso.Picasso;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(RobolectricGradleTestRunner.class)
 @Config(constants = BuildConfig.class, sdk = RobolectricConfig.sdk, packageName = RobolectricConfig.packageName)
public class IngredientsDaoAdapterTest {

    private IngredientsView view;
    private IngredientsModel model;
    private IngredientTypesDatabaseModel daoModel;
    private Picasso picasso;
    private IngredientsDaoAdapter adapter;

    @Before
    public void setup() {
        view = Mockito.mock(IngredientsView.class);
        model = Mockito.mock(IngredientsModel.class);
        daoModel = Mockito.mock(IngredientTypesDatabaseModel.class);
        picasso = Mockito.mock(Picasso.class);
        adapter = new IngredientsDaoAdapter(view, model, daoModel, picasso);
    }

    @Test
    public void testStart() throws Exception {
        adapter.onStart();

        verifyNoMoreInteractions(view, model, daoModel, picasso);
    }
}