package com.github.st1hy.countthemcalories.ui.activities.addmeal

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import com.github.st1hy.countthemcalories.ui.R
import com.github.st1hy.countthemcalories.ui.activities.addmeal.view.AddMealMenuAction
import com.github.st1hy.countthemcalories.ui.core.baseview.BaseActivity
import com.github.st1hy.countthemcalories.ui.core.rx.Functions
import com.github.st1hy.countthemcalories.ui.core.rx.Transformers
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule
import com.jakewharton.rxbinding.view.RxMenuItem
import kotlinx.android.synthetic.main.tags_app_bar.*
import rx.subjects.PublishSubject
import javax.inject.Inject

open class AddMealActivity : BaseActivity() {

    companion object {
        const val EXTRA_MEAL_PARCEL = "edit meal parcel"
        const val EXTRA_INGREDIENT_PARCEL = "edit ingredient parcel"
        const val EXTRA_NEW_MEAL_DATE = "new meal date parcel"
    }

    @Inject lateinit var fragments: Map<String, Fragment> //adds fragments to stack
    @Inject lateinit var menuActionPublishSubject: PublishSubject<AddMealMenuAction>

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_meal_activity)
        getAppComponent().newAddMealActivityComponent(ActivityModule(this))
                .inject(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.add_meal_menu, menu)
        RxMenuItem.clicks(menu.findItem(R.id.action_save))
                .map(Functions.into(AddMealMenuAction.SAVE))
                .compose(Transformers.channel(menuActionPublishSubject))
                .subscribe()
        return true
    }

}

class EditMealActivity : AddMealActivity()

class CopyMealActivity : AddMealActivity()