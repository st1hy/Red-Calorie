package com.github.st1hy.countthemcalories.activities.addmeal.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.app.Activity.RESULT_OK;
import static com.github.st1hy.countthemcalories.activities.addmeal.model.EditIngredientResult.EDIT;
import static com.github.st1hy.countthemcalories.activities.addmeal.model.EditIngredientResult.REMOVE;
import static com.github.st1hy.countthemcalories.activities.addmeal.model.EditIngredientResult.UNKNOWN;
import static com.github.st1hy.countthemcalories.activities.addmeal.model.EditIngredientResult.fromIngredientDetailResult;
import static com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.view.IngredientDetailsActivity.RESULT_REMOVE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(JUnit4.class)
public class EditIngredientResultTest {

    @Test
    public void testFromIngredientDetailResult() throws Exception {
        assertThat(fromIngredientDetailResult(RESULT_OK), equalTo(EDIT));
        assertThat(fromIngredientDetailResult(RESULT_REMOVE), equalTo(REMOVE));
        assertThat(fromIngredientDetailResult(-200), equalTo(UNKNOWN));
    }
}