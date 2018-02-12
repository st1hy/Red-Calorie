package com.github.st1hy.countthemcalories.ui.inject

import com.github.st1hy.countthemcalories.ui.activities.settings.model.SettingsModel
import com.github.st1hy.countthemcalories.ui.contract.AppComponent
import com.github.st1hy.countthemcalories.ui.contract.DaoFactories
import com.github.st1hy.countthemcalories.ui.core.headerpicture.HeaderPicturePickerUtils
import com.github.st1hy.countthemcalories.ui.inject.addingredient.AddIngredientTestComponent
import com.github.st1hy.countthemcalories.ui.inject.addmeal.AddMealTestComponent
import com.github.st1hy.countthemcalories.ui.inject.app.ApplicationModule
import com.github.st1hy.countthemcalories.ui.inject.app.SettingsModule
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = arrayOf(
        ApplicationModule::class,
        SettingsModule::class,
        DatabaseModule::class
))
interface AppTestComponent : AppComponent {

    fun getDaoSession(): DaoSession

    fun getSettingsModel(): SettingsModel

    override fun newAddIngredientActivityComponent(
            activityModule: ActivityModule): AddIngredientTestComponent

    override fun newAddMealActivityComponent(activityModule: ActivityModule): AddMealTestComponent

    fun testHeaderPicturePickerUtils(): HeaderPicturePickerUtils

    fun daoFactories() : DaoFactories
}