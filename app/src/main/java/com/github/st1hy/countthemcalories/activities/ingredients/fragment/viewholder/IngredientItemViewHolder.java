package com.github.st1hy.countthemcalories.activities.ingredients.fragment.viewholder;

import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.adapter.PositionDelegate;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.viewcontrol.ScrollingItemDelegate;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.google.common.base.Optional;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.internal.InstanceFactory;
import rx.Observable;

public class IngredientItemViewHolder extends IngredientViewHolder {


    private final IngredientTemplate reusableIngredient = new IngredientTemplate();

    private final Callback callback;
    private final ScrollingItemDelegate scrollingItemDelegate;

    @BindView(R.id.ingredients_item_button)
    View ingredientButton;
    @BindView(R.id.ingredients_item_name)
    TextView name;
    @BindView(R.id.ingredients_item_energy_density)
    TextView energyDensity;
    @BindView(R.id.ingredients_item_image)
    ImageView image;

    @BindView(R.id.ingredients_item_scrollview)
    HorizontalScrollView scrollView;

    @BindView(R.id.ingredients_item_delete_frame)
    View deleteFrame;
    @BindView(R.id.ingredients_item_edit_frame)
    View editFrame;
    @BindView(R.id.ingredients_item_content)
    View content;

    private final PositionDelegate position = new PositionDelegate();
    private final ImageHolderDelegate imageHolderDelegate;

    public IngredientItemViewHolder(@NonNull View itemView,
                                    @NonNull Callback interaction,
                                    @NonNull Picasso picasso,
                                    @NonNull PermissionsHelper permissionsHelper) {
        super(itemView);
        this.callback = interaction;
        ButterKnife.bind(this, itemView);
        scrollingItemDelegate = ScrollingItemDelegate.Builder.create()
                .setLeft(deleteFrame)
                .setCenter(content)
                .setRight(editFrame)
                .setScrollView(scrollView)
                .build();
        imageHolderDelegate = new ImageHolderDelegate(picasso, permissionsHelper,
                InstanceFactory.create(image));
    }

    @NonNull
    public IngredientTemplate getReusableIngredient() {
        return reusableIngredient;
    }


    public void setName(@NonNull String name) {
        this.name.setText(name);
    }

    public void setEnergyDensity(@NonNull String value) {
        this.energyDensity.setText(value);
    }

    @OnClick(R.id.ingredients_item_button)
    public void onContentClicked() {
        callback.onIngredientClicked(reusableIngredient, position.get());
    }

    @OnClick(R.id.ingredients_item_edit)
    public void onEditClicked() {
        callback.onEditClicked(reusableIngredient, position.get());
    }

    @OnClick(R.id.ingredients_item_delete)
    public void onDeleteClicked() {
        callback.onDeleteClicked(reusableIngredient, position.get());
    }

    public void fillParent(@NonNull final ViewGroup parent) {
        scrollingItemDelegate.fillParent(parent);
    }

    public void onAttached() {
        scrollingItemDelegate.onAttached();
        position.onAttached(callback.getEvents());
        imageHolderDelegate.onAttached();
    }

    public void onDetached() {
        scrollingItemDelegate.onDetached();
        position.onDetached();
        imageHolderDelegate.onDetached();
    }

    public void setImageUri(@NonNull Optional<Uri>uri) {
        imageHolderDelegate.setImageUri(uri);
    }

    public void setImagePlaceholder(@DrawableRes int drawableResId) {
        imageHolderDelegate.setImagePlaceholder(drawableResId);
    }

    public void setPosition(int position) {
        this.position.set(position);
    }

    public interface Callback {
        void onIngredientClicked(@NonNull IngredientTemplate ingredientTemplate, int position);

        void onDeleteClicked(@NonNull IngredientTemplate ingredientTemplate, int position);

        void onEditClicked(@NonNull IngredientTemplate ingredientTemplate, int position);

        @NonNull
        Observable<RecyclerEvent> getEvents();

    }

}
