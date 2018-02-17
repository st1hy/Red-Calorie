package com.github.st1hy.countthemcalories.ui.activities.addingredient.view

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.support.annotation.CheckResult
import com.github.st1hy.countthemcalories.ui.activities.addingredient.AddIngredientActivity
import com.github.st1hy.countthemcalories.ui.activities.tags.model.Tags
import com.github.st1hy.countthemcalories.ui.activities.tags.view.TagsActivity
import com.github.st1hy.countthemcalories.ui.contract.IngredientTemplate
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityLauncher
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityResult
import com.github.st1hy.countthemcalories.ui.core.activityresult.StartParams
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.add_ingredient_activity.*
import org.parceler.Parcels
import rx.Observable
import javax.inject.Inject


interface AddIngredientScreen {
    fun onIngredientTemplateCreated(template: IngredientTemplate)
    fun showInWebBrowser(address: Uri)
    @CheckResult fun selectTags(): Observable.Transformer<Tags, Tags>
    @CheckResult fun addTagObservable(): Observable<Void>
}

@PerActivity
class AddIngredientScreenImpl @Inject internal constructor(
        private val activity: Activity,
        private val activityLauncher: ActivityLauncher
) : AddIngredientScreen {

    companion object {
        private const val REQUEST_PICK_TAG = 0x2010
    }

    override fun onIngredientTemplateCreated(template: IngredientTemplate) {
        val intent = Intent()
        intent.putExtra(AddIngredientActivity.RESULT_INGREDIENT_TEMPLATE, Parcels.wrap(template))
        activity.setResult(RESULT_OK, intent)
        activity.finish()
    }


    @CheckResult
    override fun selectTags() = Observable.Transformer<Tags, Tags> { observable ->
        observable.map(this::getSelectTagsParams)
                .compose(activityLauncher.startActivityForResult(REQUEST_PICK_TAG))
                .filter(ActivityResult.IS_OK)
                .map(this::tagsFromResults)
    }

    override fun showInWebBrowser(address: Uri) {
        activity.startActivity(Intent(Intent.ACTION_VIEW, address))
    }

    override fun addTagObservable(): Observable<Void> {
        return RxView.clicks(activity.add_ingredient_fab_add_tag)
    }

    private fun tagsFromResults(activityResult: ActivityResult): Tags? {
        val data = activityResult.data ?: return null
        return Parcels.unwrap<Tags>(data.getParcelableExtra<Parcelable>(
                TagsActivity.extraTags))
    }

    private fun getSelectTagsParams(tags: Tags): StartParams {
        val intent = Intent(activity, TagsActivity::class.java)
        intent.action = TagsActivity.actionPickTag
        if (!tags.isEmpty) {
            intent.putExtra(TagsActivity.extraSelectedTags,
                    Parcels.wrap(tags))
        }
        return StartParams.of(intent, REQUEST_PICK_TAG)
    }

}
