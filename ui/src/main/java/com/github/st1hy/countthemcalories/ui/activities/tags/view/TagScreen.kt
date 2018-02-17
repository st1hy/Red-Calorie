package com.github.st1hy.countthemcalories.ui.activities.tags.view

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.support.annotation.CheckResult
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.view.View
import com.github.st1hy.countthemcalories.ui.R
import com.github.st1hy.countthemcalories.ui.activities.ingredients.IngredientsActivity
import com.github.st1hy.countthemcalories.ui.activities.tags.model.Tags
import com.github.st1hy.countthemcalories.ui.core.baseview.Click
import com.github.st1hy.countthemcalories.ui.core.rx.Functions
import com.github.st1hy.countthemcalories.ui.core.state.Visibility
import com.github.st1hy.countthemcalories.ui.core.tokensearch.RxSearchable
import com.github.st1hy.countthemcalories.ui.core.tokensearch.TokenSearchView
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.tags_app_bar.*
import org.parceler.Parcels
import rx.Observable
import javax.inject.Inject

internal interface TagsScreen {
    fun openIngredientsFilteredBy(tagName: String)
    fun onTagsSelected(tags: Tags)
    fun setConfirmButtonVisibility(visibility: Visibility)
    @CheckResult fun addTagClickedObservable(): Observable<Click>
    @CheckResult fun confirmClickedObservable(): Observable<Click>
    @CheckResult fun getQueryObservable(): Observable<CharSequence>
}

@PerActivity internal class TagsScreenImpl @Inject constructor(
        private val activity: Activity,
        private val searchView: TokenSearchView
) : TagsScreen {
    
    private var addNew: FloatingActionButton = activity.tags_fab_add_new
    private var confirm: FloatingActionButton = activity.tags_fab_confirm

    override fun addTagClickedObservable(): Observable<Click> {
        return RxView.clicks(addNew).map(Functions.into(Click.get()))
    }


    override fun openIngredientsFilteredBy(tagName: String) {
        val intent = Intent(activity, IngredientsActivity::class.java)
        intent.putExtra(IngredientsActivity.EXTRA_TAG_FILTER_STRING, tagName)
        activity.startActivity(intent)
    }

    override fun getQueryObservable(): Observable<CharSequence> =
            RxSearchable.create(searchView).map { searchable ->  searchable.query }

    override fun onTagsSelected(tags: Tags) {
        val data = Intent()
        data.putExtra(TagsActivity.extraTags, Parcels.wrap(tags))
        activity.setResult(RESULT_OK, data)
        activity.finish()
    }

    override fun setConfirmButtonVisibility(visibility: Visibility) {
        confirm.visibility = visibility.visibility
        val layoutParams = addNew.layoutParams as CoordinatorLayout.LayoutParams
        if (visibility.isVisible) {
            addNew.size = FloatingActionButton.SIZE_MINI
            layoutParams.anchorId = R.id.confirm_space

        } else {
            addNew.size = FloatingActionButton.SIZE_NORMAL
            layoutParams.anchorId = View.NO_ID
        }
        addNew.layoutParams = layoutParams
    }

    override fun confirmClickedObservable(): Observable<Click> {
        return RxView.clicks(confirm).map(Functions.into(Click.get()))
    }
}
