package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.core.rx.Filters;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.activityresult.ActivityResult;
import com.github.st1hy.countthemcalories.core.rx.activityresult.RxActivityResult;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Tag;

import org.parceler.Parcels;

import java.util.Collection;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

import static android.app.Activity.RESULT_OK;

public class AddIngredientScreenImpl implements AddIngredientScreen {

    private static final int REQUEST_PICK_TAG = 0x2010;

    @NonNull private final Activity activity;
    @NonNull private final RxActivityResult rxActivityResult;
    @NonNull private final PublishSubject<AddIngredientMenuAction> menuActions;

    @Inject
    public AddIngredientScreenImpl(@NonNull Activity activity,
                                   @NonNull RxActivityResult rxActivityResult,
                                   @NonNull PublishSubject<AddIngredientMenuAction> menuActions) {
        this.activity = activity;
        this.rxActivityResult = rxActivityResult;
        this.menuActions = menuActions;
    }

    @Override
    public void onIngredientTemplateCreated(@NonNull IngredientTemplate template) {
        Intent intent = new Intent();
        intent.putExtra(AddIngredientActivity.RESULT_INGREDIENT_ID_LONG, template.getId());
        activity.setResult(RESULT_OK, intent);
        activity.finish();
    }


    @Override
    @NonNull
    public Observable<Tag> selectTag(@NonNull Collection<String> excludedTagNames) {
        Intent intent = new Intent(activity, TagsActivity.class);
        intent.setAction(TagsActivity.ACTION_PICK_TAG);
        if (!excludedTagNames.isEmpty()) {
            String[] tags = excludedTagNames.toArray(new String[excludedTagNames.size()]);
            intent.putExtra(TagsActivity.EXTRA_EXCLUDE_TAG_STRING_ARRAY, tags);
        }
        return rxActivityResult.from(activity).startActivityForResult(intent, REQUEST_PICK_TAG)
                .filter(ActivityResult.IS_OK)
                .map(new Func1<ActivityResult, Tag>() {
                    @Override
                    public Tag call(ActivityResult activityResult) {
                        Intent data = activityResult.getData();
                        if (data == null) return null;
                        return Parcels.unwrap(data.getParcelableExtra(TagsActivity.EXTRA_TAG));
                    }
                });
    }


    @NonNull
    @Override
    public Observable<Void> getSaveObservable() {
        return menuActions
                .filter(Filters.equalTo(AddIngredientMenuAction.SAVE))
                .map(Functions.INTO_VOID);
    }

    @Override
    public void showInWebBrowser(@NonNull Uri address) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, address));
    }
}
