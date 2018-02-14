package com.github.st1hy.countthemcalories.ui.activities.tags.view;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.ui.activities.tags.model.Tags;
import com.github.st1hy.countthemcalories.ui.core.baseview.Click;
import com.github.st1hy.countthemcalories.ui.core.rx.Functions;
import com.github.st1hy.countthemcalories.ui.core.state.Visibility;
import com.github.st1hy.countthemcalories.ui.core.tokensearch.RxSearchable;
import com.github.st1hy.countthemcalories.ui.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.ui.core.tokensearch.TokenSearchView;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.jakewharton.rxbinding.view.RxView;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static android.app.Activity.RESULT_OK;

@PerActivity
public class TagsScreenImpl implements TagsScreen {

    @NonNull
    private final Activity activity;
    @NonNull
    private final TokenSearchView searchView;
    @BindView(R.id.tags_fab_add_new)
    FloatingActionButton addNew;
    @BindView(R.id.tags_fab_confirm)
    FloatingActionButton confirm;

    @Inject
    public TagsScreenImpl(@NonNull Activity activity,
                          @NonNull TokenSearchView searchView) {
        this.activity = activity;
        this.searchView = searchView;
        ButterKnife.bind(this, activity);
    }

    @NonNull
    @Override
    public Observable<Click> addTagClickedObservable() {
        return RxView.clicks(addNew).map(Functions.into(Click.get()));
    }


    @Override
    public void openIngredientsFilteredBy(@NonNull String tagName) {
        Intent intent = new Intent(activity, IngredientsActivity.class);
        intent.putExtra(IngredientsActivity.EXTRA_TAG_FILTER_STRING, tagName);
        activity.startActivity(intent);
    }

    @NonNull
    @Override
    public Observable<CharSequence> getQueryObservable() {
        return RxSearchable.create(searchView).map(SearchResult::getQuery);
    }

    @Override
    public void onTagsSelected(@NonNull Tags tags) {
        Intent data = new Intent();
        data.putExtra(TagsActivity.extraTags, Parcels.wrap(tags));
        activity.setResult(RESULT_OK, data);
        activity.finish();
    }

    @Override
    public void setConfirmButtonVisibility(@NonNull Visibility visibility) {
        //noinspection WrongConstant
        confirm.setVisibility(visibility.getVisibility());
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) addNew.getLayoutParams();
        if (visibility.isVisible()) {
            addNew.setSize(FloatingActionButton.SIZE_MINI);
            layoutParams.setAnchorId(R.id.confirm_space);

        } else {
            addNew.setSize(FloatingActionButton.SIZE_NORMAL);
            layoutParams.setAnchorId(View.NO_ID);
        }
        addNew.setLayoutParams(layoutParams);
    }

    @NonNull
    @Override
    public Observable<Click> confirmClickedObservable() {
        return RxView.clicks(confirm).map(Functions.into(Click.get()));
    }
}
