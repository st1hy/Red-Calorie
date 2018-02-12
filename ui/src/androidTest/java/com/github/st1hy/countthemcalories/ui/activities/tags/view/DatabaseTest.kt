package com.github.st1hy.countthemcalories.ui.activities.tags.view

import android.net.Uri
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.github.st1hy.countthemcalories.ui.contract.CreationSource
import com.github.st1hy.countthemcalories.ui.contract.DaoFactories
import com.github.st1hy.countthemcalories.ui.contract.IngredientTemplate
import com.github.st1hy.countthemcalories.ui.contract.Tag
import com.github.st1hy.countthemcalories.ui.rules.ApplicationComponentRule
import com.github.st1hy.countthemcalories.ui.rules.getTestComponent
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import java.util.ArrayList


@RunWith(AndroidJUnit4::class)
@LargeTest
@Ignore("Only for fake data population")
class DatabaseTest {

    companion object {
        private const val SIZE = 100000
    }

    private val componentRule = ApplicationComponentRule(getTargetContext())
    val main = IntentsTestRule(TagsActivity::class.java, false, false)

    @Rule
    val rule: TestRule = RuleChain.outerRule(componentRule).around(main)

    private fun getEntries(dao: DaoFactories): Iterable<Tag> {
        return (0 until SIZE).mapTo(ArrayList(SIZE)) { position ->
            dao.newTag().apply {
                id = position.toLong()
                name = "Tag " + position
            }
        }
    }

    private fun getIngredientEntries(dao: DaoFactories): Iterable<IngredientTemplate> {
        val ingredients = ArrayList<IngredientTemplate>(SIZE)
        for (i in 0 until SIZE) {
            val type = if (i % 2 == 0) AmountUnitType.MASS else AmountUnitType.VOLUME
            val amount = i % 71 + i % 97 / 100.0
            val template = dao.newIngredientTemplate().apply {
                id = i.toLong()
                name = "Ingredient " + i
                uri = Uri.EMPTY
                source = CreationSource.USER
                type = type
                amount = amount
            }
            ingredients.add(template)
        }
        return ingredients
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val component = getTestComponent()
        val session = component.getDaoSession()
        session.getTagDao().deleteAll()
        session.getTagDao().insertOrReplaceInTx(getEntries())

        session.getIngredientTemplateDao().deleteAll()
        session.getIngredientTemplateDao().insertOrReplaceInTx(getIngredientEntries())

        session.getJointIngredientTagDao().deleteAll()
        session.getMealDao().deleteAll()
        session.getIngredientDao().deleteAll()
    }


    @Test
    @Throws(Exception::class)
    fun testInsert() {
    }
}