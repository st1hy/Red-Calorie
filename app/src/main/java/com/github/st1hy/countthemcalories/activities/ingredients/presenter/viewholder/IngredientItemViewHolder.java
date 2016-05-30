package com.github.st1hy.countthemcalories.activities.ingredients.presenter.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.core.viewcontrol.ScrollingItemDelegate;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

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

    private int position;

    private final Action1<RecyclerEvent> eventListener = new Action1<RecyclerEvent>() {
        @Override
        public void call(RecyclerEvent event) {
            onRecyclerEvent(event);
        }
    };

    public IngredientItemViewHolder(@NonNull View itemView,
                                    @NonNull Callback interaction) {
        super(itemView);
        this.callback = interaction;
        ButterKnife.bind(this, itemView);
        scrollingItemDelegate = ScrollingItemDelegate.Builder.create()
                .setLeft(deleteFrame)
                .setCenter(content)
                .setRight(editFrame)
                .setScrollView(scrollView)
                .build();
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

    @NonNull
    public ImageView getImage() {
        return image;
    }

    @OnClick(R.id.ingredients_item_button)
    public void onContentClicked() {
        callback.onIngredientClicked(reusableIngredient, position);
    }

    @OnClick(R.id.ingredients_item_edit)
    public void onEditClicked() {
        callback.onEditClicked(reusableIngredient, position);
    }

    @OnClick(R.id.ingredients_item_delete)
    public void onDeleteClicked() {
        callback.onDeleteClicked(reusableIngredient, position);
    }

    public void fillParent(@NonNull final ViewGroup parent) {
        scrollingItemDelegate.fillParent(parent);
    }

    public void onAttached() {
        scrollingItemDelegate.onAttached();
        scrollingItemDelegate.subscribe(callback.getEvents()
                .subscribe(eventListener));
    }

    public void onDetached() {
        scrollingItemDelegate.onDetached();
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private void onRecyclerEvent(@NonNull RecyclerEvent event) {
        switch (event.getType()) {
            case ADDED:
                if (position >= event.getPosition()) {
                    position++;
                }
                break;
            case REMOVED:
                if (position > event.getPosition()) {
                    position--;
                }
                break;
        }
    }

    public interface Callback {
        void onIngredientClicked(@NonNull IngredientTemplate ingredientTemplate, int position);

        void onDeleteClicked(@NonNull IngredientTemplate ingredientTemplate, int position);

        void onEditClicked(@NonNull IngredientTemplate ingredientTemplate, int position);

        @NonNull
        Observable<RecyclerEvent> getEvents();
    }

}
