package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.jakewharton.rxbinding.support.v4.view.RxMenuItemCompat;
import com.jakewharton.rxbinding.view.RxMenuItem;
import com.jakewharton.rxbinding.view.RxView;

import java.util.Collection;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;

import static android.app.Activity.RESULT_OK;
import static android.os.Build.VERSION_CODES.M;

public class AddIngredientScreenImpl implements AddIngredientScreen {

    @NonNull private final Activity activity;

    final PublishSubject<Void> saveClickedSubject = PublishSubject.create();

    @Inject
    public AddIngredientScreenImpl(@NonNull Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onIngredientTemplateCreated(@NonNull IngredientTemplate template) {
        Intent intent = new Intent();
        intent.putExtra(AddIngredientActivity.RESULT_INGREDIENT_ID_LONG, template.getId());
        activity.setResult(RESULT_OK, intent);
        activity.finish();
    }


    @Override
    public void openSelectTagScreen(@NonNull Collection<String> tagNames) {
        Intent intent = new Intent(activity, TagsActivity.class);
        intent.setAction(TagsActivity.ACTION_PICK_TAG);
        if (!tagNames.isEmpty()) {
            String[] tags = tagNames.toArray(new String[tagNames.size()]);
            intent.putExtra(TagsActivity.EXTRA_EXCLUDE_TAG_STRING_ARRAY, tags);
        }
        startActivityForResult(intent, REQUEST_PICK_TAG);
    }


    @NonNull
    @Override
    public Observable<Void> getSaveObservable() {
        RxMenuItem.clicks()
        return saveClickedSubject;
    }

    @Override
    public void showInWebBrowser(@NonNull Uri address) {
        startActivity(new Intent(Intent.ACTION_VIEW, address));
    }
}
