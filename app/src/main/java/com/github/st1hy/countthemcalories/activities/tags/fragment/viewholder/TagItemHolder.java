package com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.TagsViewModel;
import com.github.st1hy.countthemcalories.core.adapter.PositionDelegate;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.core.rx.Transformers;
import com.github.st1hy.countthemcalories.core.viewcontrol.ScrollingItemDelegate;
import com.github.st1hy.countthemcalories.database.Tag;
import com.jakewharton.rxbinding.widget.RxCompoundButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.subjects.PublishSubject;

public class TagItemHolder extends TagViewHolder {

    @BindView(R.id.tags_item_name)
    TextView name;
    @BindView(R.id.tags_item_count)
    TextView counter;
    @BindView(R.id.tags_item_checkbox)
    CheckBox checkBox;

    @BindView(R.id.tags_item_scrollview)
    HorizontalScrollView scrollView;
    @BindView(R.id.tags_item_delete_frame)
    View deleteFrame;
    @BindView(R.id.tags_item_edit_frame)
    View editFrame;
    @BindView(R.id.tag_item_button)
    View content;

    private final TagsViewModel viewModel;
    private final OnTagInteraction callback;
    private final Tag tag = new Tag();
    private final PositionDelegate position = new PositionDelegate();
    private final ScrollingItemDelegate scrollingItemDelegate;

    public TagItemHolder(@NonNull View itemView,
                         @NonNull TagsViewModel viewModel,
                         @NonNull OnTagInteraction callback) {
        super(itemView);
        this.viewModel = viewModel;
        this.callback = callback;
        ButterKnife.bind(this, itemView);
        scrollingItemDelegate = ScrollingItemDelegate.Builder.create()
                .setScrollView(scrollView)
                .setLeft(deleteFrame)
                .setCenter(content)
                .setRight(editFrame)
                .build();
    }

    @OnClick(R.id.tag_item_button)
    public void onClick() {
        callback.onTagClicked(position.get(), this);
    }


    @OnClick(R.id.tags_item_delete)
    public void onDeleteClicked() {
        callback.onDeleteClicked(position.get(), this);
    }

    @OnClick(R.id.tags_item_edit)
    public void onEditClicked() {
        callback.onEditClicked(position.get(), this);
    }

    @NonNull
    public Tag getReusableTag() {
        return tag;
    }

    public void bind(int position, @NonNull Tag tag) {
        this.position.set(position);
        name.setText(tag.getName());
        counter.setText(viewModel.getCounterFor(tag.getIngredientCount()));
    }

    @Override
    public void onAttached(@NonNull Observable<RecyclerEvent> events,
                           @NonNull PublishSubject<TagViewHolder> stateChanges) {
        position.onAttached(events);
        scrollingItemDelegate.onAttached();
        scrollingItemDelegate.subscribe(
                RxCompoundButton.checkedChanges(checkBox)
                        .map(isChecked -> TagItemHolder.this)
                        .compose(Transformers.channel(stateChanges))
                        .subscribe()
        );
    }

    @Override
    public void onDetached() {
        position.onDetached();
        scrollingItemDelegate.onDetached();
    }

    public void fillParent(@NonNull ViewGroup parent) {
        scrollingItemDelegate.fillParent(parent);
    }

    public void setSelectable(boolean inSelectMode) {
        checkBox.setEnabled(inSelectMode);
    }

    public void setChecked(boolean isSelected) {
        checkBox.setChecked(isSelected);
    }

    public void setCheckedTint(@ColorInt int color) {
        Drawable buttonDrawable = CompoundButtonCompat.getButtonDrawable(checkBox);
        if (buttonDrawable != null) {
            DrawableCompat.setTint(buttonDrawable, color);
            checkBox.setButtonDrawable(buttonDrawable);
        }
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }
}
