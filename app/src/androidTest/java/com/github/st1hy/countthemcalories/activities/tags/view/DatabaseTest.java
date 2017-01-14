package com.github.st1hy.countthemcalories.activities.tags.view;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.st1hy.countthemcalories.activities.tags.TagsActivity;
import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.rules.ApplicationComponentRule;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.ArrayList;

import static android.support.test.InstrumentationRegistry.getTargetContext;

@RunWith(AndroidJUnit4.class)
@LargeTest
@Ignore("Only for fake data population")
public class DatabaseTest {

    private final ApplicationComponentRule componentRule = new ApplicationComponentRule(getTargetContext());
    public final IntentsTestRule<TagsActivity> main = new IntentsTestRule<>(TagsActivity.class, false, false);

    @Rule
    public final TestRule rule = RuleChain.outerRule(componentRule).around(main);

    private static final int SIZE = 100_000;


    @Before
    public void setUp() throws Exception {
        ApplicationTestComponent component = (ApplicationTestComponent) ((CaloriesCounterApplication) getTargetContext().getApplicationContext()).getComponent();
        DaoSession session = component.getDaoSession();
        session.getTagDao().deleteAll();
        session.getTagDao().insertOrReplaceInTx(getEntries());

        session.getIngredientTemplateDao().deleteAll();
        session.getIngredientTemplateDao().insertOrReplaceInTx(getIngredientEntries());

        session.getJointIngredientTagDao().deleteAll();
        session.getMealDao().deleteAll();
        session.getIngredientDao().deleteAll();
    }

    @NonNull
    private Iterable<Tag> getEntries() {
        ArrayList<Tag> tags = new ArrayList<>(SIZE);
        for (long i = 0; i < SIZE; i++) {
            tags.add(new Tag(i, "Tag " + i));
        }
        return tags;
    }


    @Test
    public void testInsert() throws Exception {
    }

    @NonNull
    private Iterable<IngredientTemplate> getIngredientEntries() {
        ArrayList<IngredientTemplate> ingredients = new ArrayList<>(SIZE);
        for (int i = 0; i < SIZE; i++) {
            AmountUnitType type = i % 2 == 0 ? AmountUnitType.MASS : AmountUnitType.VOLUME;
            BigDecimal amount = new BigDecimal("" + i % 71 + "." + i % 97);
            ingredients.add(new IngredientTemplate((long)i, "Ingredient "+ i, Uri.EMPTY, DateTime.now(), type, amount));
        }
        return ingredients;
    }
}
