package com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.Transformers;
import com.github.st1hy.countthemcalories.core.viewcontrol.ScrollingItemDelegate;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.inject.activities.overview.fragment.mealitems.PerMealRow;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.internal.InstanceFactory;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems.MealInteraction.Type.DELETE;
import static com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems.MealInteraction.Type.EDIT;
import static com.github.st1hy.countthemcalories.activities.overview.fragment.mealitems.MealInteraction.Type.OPEN;

@PerMealRow
public class MealItemHolder extends AbstractMealItemHolder {

    private final ScrollingItemDelegate scrollingItemDelegate;
    private final ImageHolderDelegate imageHolderDelegate;

    private Meal meal;

    @BindView(R.id.overview_item_image)
    ImageView image;
    @BindView(R.id.overview_item_name)
    TextView name;
    @BindView(R.id.overview_item_energy)
    TextView totalEnergy;
    @BindView(R.id.overview_item_ingredients)
    TextView ingredients;
    @BindView(R.id.overview_item_date)
    TextView date;
    @BindView(R.id.overview_item_content)
    View content;
    @BindView(R.id.overview_item_scrollview)
    HorizontalScrollView scrollView;
    @BindView(R.id.overview_item_delete_frame)
    View deleteFrame;
    @BindView(R.id.overview_item_edit_frame)
    View editFrame;
    @BindView(R.id.overview_item_edit)
    View editButton;
    @BindView(R.id.overview_item_delete)
    View deleteButton;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public MealItemHolder(@NonNull @Named("mealItemRoot") View itemView,
                          @NonNull Picasso picasso,
                          @NonNull PermissionsHelper permissionHelper) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        scrollingItemDelegate = ScrollingItemDelegate.Builder.create()
                .setLeft(deleteFrame)
                .setCenter(content)
                .setRight(editFrame)
                .setScrollView(scrollView)
                .build();
        this.imageHolderDelegate = new ImageHolderDelegate(picasso, permissionHelper,
                InstanceFactory.create(image));
    }

    public void fillParent(@NonNull final ViewGroup parent) {
        scrollingItemDelegate.fillParent(parent);
    }

    @NonNull
    public ImageView getImage() {
        return image;
    }

    public void setName(@NonNull String name) {
        this.name.setText(name);
    }

    public void setTotalEnergy(@NonNull String totalEnergy) {
        this.totalEnergy.setText(totalEnergy);
    }

    public void setIngredients(@NonNull String ingredients) {
        this.ingredients.setText(ingredients);
    }

    public void setDate(@NonNull String date) {
        this.date.setText(date);
    }

    public void setMeal(@NonNull Meal meal) {
        this.meal = meal;
    }

    public Meal getMeal() {
        return meal;
    }

    @Override
    public void onAttached(@NonNull PublishSubject<MealInteraction> subject) {
        scrollingItemDelegate.onAttached();
        imageHolderDelegate.onAttached();
        subscriptions.add(
                RxView.clicks(content).map(Functions.into(OPEN))
                        .mergeWith(RxView.clicks(editButton).map(Functions.into(EDIT)))
                        .mergeWith(RxView.clicks(deleteButton).map(Functions.into(DELETE)))
                        .map(type -> MealInteraction.of(type, this))
                        .compose(Transformers.channel(subject))
                        .subscribe()
        );
    }

    @Override
    public void onDetached() {
        scrollingItemDelegate.onDetached();
        imageHolderDelegate.onDetached();
        subscriptions.clear();
    }

    public void setEnabled(boolean enabled) {
        content.setEnabled(enabled);
        editButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
    }

    public void setImageUri(@NonNull Uri uri) {
        imageHolderDelegate.displayImage(uri);
    }

}