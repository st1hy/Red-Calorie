package com.github.st1hy.countthemcalories.ui.activities.addingredient.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.ui.activities.addingredient.AddIngredientActivity;
import com.github.st1hy.countthemcalories.ui.activities.tags.model.Tags;
import com.github.st1hy.countthemcalories.ui.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.ui.activities.tags.view.TagsActivityBundle;
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityLauncher;
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityResult;
import com.github.st1hy.countthemcalories.ui.core.activityresult.StartParams;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.jakewharton.rxbinding.view.RxView;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static android.app.Activity.RESULT_OK;

@PerActivity
public class AddIngredientScreenImpl implements AddIngredientScreen {

    private static final int REQUEST_PICK_TAG = 0x2010;

    @NonNull
    private final Activity activity;
    @NonNull
    private final ActivityLauncher activityLauncher;

    @BindView(R.id.add_ingredient_fab_add_tag)
    FloatingActionButton addTagFab;

    @Inject
    AddIngredientScreenImpl(@NonNull Activity activity,
                            @NonNull ActivityLauncher activityLauncher) {
        this.activity = activity;
        this.activityLauncher = activityLauncher;
        ButterKnife.bind(this, activity);
    }

    @Override
    public void onIngredientTemplateCreated(@NonNull IngredientTemplate template) {
        Intent intent = new Intent();
        intent.putExtra(AddIngredientActivity.RESULT_INGREDIENT_TEMPLATE, Parcels.wrap(template));
        activity.setResult(RESULT_OK, intent);
        activity.finish();
    }


    @Override
    @NonNull
    @CheckResult
    public Observable.Transformer<Tags, Tags> selectTags() {
        return paramsObservable -> paramsObservable
                .map(tags -> {
                    Intent intent = new Intent(activity, TagsActivity.class);
                    intent.setAction(TagsActivity.actionPickTag);
                    if (!tags.isEmpty()) {
                        intent.putExtra(TagsActivity.extraSelectedTags,
                                Parcels.wrap(tags));
                    }
                    return StartParams.of(intent, REQUEST_PICK_TAG);
                })
                .compose(activityLauncher.startActivityForResult(REQUEST_PICK_TAG))
                .filter(ActivityResult.IS_OK)
                .map(activityResult -> {
                    Intent data = activityResult.getData();
                    if (data == null) return null;
                    return Parcels.unwrap(data.getParcelableExtra(
                            TagsActivity.extraTags));
                });
    }

    @Override
    public void showInWebBrowser(@NonNull Uri address) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, address));
    }

    @NonNull
    @Override
    public Observable<Void> addTagObservable() {
        return RxView.clicks(addTagFab);
    }
}
