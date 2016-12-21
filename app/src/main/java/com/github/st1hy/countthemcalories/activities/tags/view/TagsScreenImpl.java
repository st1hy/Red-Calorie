package com.github.st1hy.countthemcalories.activities.tags.view;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.tags.TagsActivity;
import com.github.st1hy.countthemcalories.core.tokensearch.RxSearchable;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.core.tokensearch.TokenSearchView;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.inject.PerActivity;
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
    @BindView(R.id.tags_add_new)
    FloatingActionButton fab;

    @Inject
    public TagsScreenImpl(@NonNull Activity activity,
                          @NonNull TokenSearchView searchView) {
        this.activity = activity;
        this.searchView = searchView;
        ButterKnife.bind(this, activity);
    }

    @NonNull
    @Override
    public Observable<Void> getAddTagClickedObservable() {
        return RxView.clicks(fab);
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
    public void onTagSelected(@NonNull Tag tag) {
        Intent data = new Intent();
        data.putExtra(TagsActivity.EXTRA_TAG, Parcels.wrap(tag));
        activity.setResult(RESULT_OK, data);
        activity.finish();
    }
}
