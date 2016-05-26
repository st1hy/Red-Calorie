package com.github.st1hy.countthemcalories.activities.ingredients.presenter.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.HorizontalScrollObservable;
import com.github.st1hy.countthemcalories.core.adapter.callbacks.OnItemInteraction;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.jakewharton.rxbinding.internal.Functions;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.view.ViewScrollChangeEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class IngredientItemViewHolder extends IngredientViewHolder implements View.OnClickListener {


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
    @BindView(R.id.ingredients_item_delete)
    ImageButton delete;
    @BindView(R.id.ingredients_item_edit)
    ImageButton edit;

    @BindView(R.id.ingredients_item_content)
    View content;
    private final IngredientTemplate reusableIngredient = new IngredientTemplate();

    private final OnItemInteraction<IngredientTemplate> callback;

    public IngredientItemViewHolder(@NonNull View itemView,
                                    @NonNull OnItemInteraction<IngredientTemplate> interaction) {
        super(itemView);
        this.callback = interaction;
        ButterKnife.bind(this, itemView);
        ingredientButton.setOnClickListener(this);
        HorizontalScrollObservable.create(scrollView)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ViewScrollChangeEvent>() {
                    @Override
                    public void call(ViewScrollChangeEvent viewScrollChangeEvent) {
                        onFinishedScroll(viewScrollChangeEvent.scrollX());
                    }
                });
        resetScroll();
    }


    private void onFinishedScroll(int scrollX) {
        int toDelete = Math.abs(scrollX - delete.getLeft());
        int toContent = Math.abs(scrollX - content.getLeft());
        int toEdit = Math.abs(scrollX + content.getWidth() - edit.getRight());
        if (toContent <= toDelete && toContent <= toEdit) {
            if (toContent > 0) scrollView.smoothScrollTo(content.getLeft(), 0);
        } else if (toDelete <= toEdit) {
            if (toDelete > 0) scrollView.smoothScrollTo(delete.getLeft(), 0);
        } else {
            if (toEdit > 0) scrollView.smoothScrollTo(edit.getLeft(), 0);
        }
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

    @Override
    public void onClick(View v) {
        callback.onItemClicked(reusableIngredient);
    }

    public void fillParent(@NonNull final ViewGroup parent) {
        int parentWidth = parent.getWidth();
        ViewGroup.LayoutParams layoutParams = content.getLayoutParams();
        layoutParams.width = parentWidth;
        content.setLayoutParams(layoutParams);
    }

    public void resetScroll() {
        if (content.getLeft() != 0) {
            resetScrollInternal();
        } else {
            RxView.preDraws(content, Functions.FUNC0_ALWAYS_TRUE)
                    .take(1)
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            resetScroll();
                        }
                    });
        }
    }

    private void resetScrollInternal() {
        scrollView.scrollTo(content.getLeft(), 0);
    }

}
