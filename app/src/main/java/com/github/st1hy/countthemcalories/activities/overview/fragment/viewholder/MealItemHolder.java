package com.github.st1hy.countthemcalories.activities.overview.fragment.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.viewcontrol.ScrollingItemDelegate;
import com.github.st1hy.countthemcalories.database.Meal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MealItemHolder extends AbstractMealItemHolder {

    private final Callback callback;
    private final ScrollingItemDelegate scrollingItemDelegate;

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


    public MealItemHolder(@NonNull View itemView, Callback callback) {
        super(itemView);
        this.callback = callback;
        ButterKnife.bind(this, itemView);
        scrollingItemDelegate = ScrollingItemDelegate.Builder.create()
                .setLeft(deleteFrame)
                .setCenter(content)
                .setRight(editFrame)
                .setScrollView(scrollView)
                .build();
    }

    @OnClick(R.id.overview_item_content)
    public void onContentClicked() {
        callback.onMealClicked(meal, image);
    }

    @OnClick(R.id.overview_item_edit)
    public void onEditClicked() {
        callback.onEditClicked(meal);
    }

    @OnClick(R.id.overview_item_delete)
    public void onDeleteClicked() {
        callback.onDeleteClicked(meal);
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

    public void onAttached() {
        scrollingItemDelegate.onAttached();
    }

    public void onDetached() {
        scrollingItemDelegate.onDetached();
    }

    public interface Callback {
        void onMealClicked(@NonNull Meal meal, @NonNull View sharedImage);

        void onDeleteClicked(@NonNull Meal meal);

        void onEditClicked(@NonNull Meal meal);
    }
}