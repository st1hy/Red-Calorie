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
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.jakewharton.rxbinding.internal.Functions;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.view.ViewScrollChangeEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class IngredientItemViewHolder extends IngredientViewHolder {


    private final Action1<Void> resetScrollOnPreDraw = new Action1<Void>() {
        @Override
        public void call(Void aVoid) {
            resetScroll();
        }
    };
    private final Action1<ViewScrollChangeEvent> scrollToNearest = new Action1<ViewScrollChangeEvent>() {
        @Override
        public void call(ViewScrollChangeEvent viewScrollChangeEvent) {
            onFinishedScroll(viewScrollChangeEvent.scrollX());
        }
    };
    private final Action1<Void> scrollToContent = new Action1<Void>() {
        @Override
        public void call(Void aVoid) {
            scrollView.smoothScrollTo(content.getLeft(), 0);
        }
    };

    private final IngredientTemplate reusableIngredient = new IngredientTemplate();
    private final Callback callback;
    private final HorizontalScrollObservable scrollingObservable;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

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
    @BindView(R.id.ingredients_item_delete_frame)
    View deleteFrame;

    @BindView(R.id.ingredients_item_edit)
    ImageButton edit;
    @BindView(R.id.ingredients_item_edit_frame)
    View editFrame;

    @BindView(R.id.ingredients_item_content)
    View content;
    private int position;

    public IngredientItemViewHolder(@NonNull View itemView,
                                    @NonNull Callback interaction) {
        super(itemView);
        this.callback = interaction;
        ButterKnife.bind(this, itemView);
        scrollingObservable = HorizontalScrollObservable.create(scrollView);
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
//        subscriptions.clear();
        callback.onEditClicked(reusableIngredient, position);
    }

    @OnClick(R.id.ingredients_item_delete)
    public void onDeleteClicked() {
//        subscriptions.clear();
        callback.onDeleteClicked(reusableIngredient, position);
    }

    public void fillParent(@NonNull final ViewGroup parent) {
        int parentWidth = parent.getWidth();
        ViewGroup.LayoutParams layoutParams = content.getLayoutParams();
        layoutParams.width = parentWidth;
        content.setLayoutParams(layoutParams);
    }

    public void onAttached() {
        resetScroll();
        subscriptions.add(scrollingObservable.getScrollToPositionObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scrollToNearest));
        subscriptions.add(scrollingObservable.getIdleObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scrollToContent));
    }

    public void onDetached() {
        subscriptions.clear();
        resetScroll();
    }

    private void resetScroll() {
        if (content.getLeft() != 0) {
            resetScrollInternal();
        } else {
            subscriptions.add(RxView.preDraws(content, Functions.FUNC0_ALWAYS_TRUE)
                    .take(1)
                    .subscribe(resetScrollOnPreDraw));
        }
    }

    private void resetScrollInternal() {
        scrollView.scrollTo(content.getLeft(), 0);
    }

    private void onFinishedScroll(int scrollX) {
        int toDelete = Math.abs(scrollX - deleteFrame.getLeft());
        int toContent = Math.abs(scrollX - content.getLeft());
        int toEdit = Math.abs(scrollX + content.getWidth() - editFrame.getRight());
        if (toContent <= toDelete && toContent <= toEdit) {
            if (toContent > 0) scrollView.smoothScrollTo(content.getLeft(), 0);
        } else if (toDelete <= toEdit) {
            if (toDelete > 0) scrollView.smoothScrollTo(deleteFrame.getLeft(), 0);
        } else {
            if (toEdit > 0) scrollView.smoothScrollTo(editFrame.getLeft(), 0);
        }
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public interface Callback {
        void onIngredientClicked(@NonNull IngredientTemplate ingredientTemplate, int position);
        void onDeleteClicked(@NonNull IngredientTemplate ingredientTemplate, int position);
        void onEditClicked(@NonNull IngredientTemplate ingredientTemplate, int position);
    }

}
