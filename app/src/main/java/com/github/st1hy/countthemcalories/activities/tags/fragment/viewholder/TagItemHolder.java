package com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder;

import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.TagsViewModel;
import com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder.inject.TagRootView;
import com.github.st1hy.countthemcalories.core.adapter.PositionDelegate;
import com.github.st1hy.countthemcalories.core.rx.Transformers;
import com.github.st1hy.countthemcalories.core.viewcontrol.ScrollingItemDelegate;
import com.github.st1hy.countthemcalories.database.Tag;
import com.jakewharton.rxbinding.widget.RxCompoundButton;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.subjects.PublishSubject;

public class TagItemHolder extends TagViewHolder {

    @BindView(R.id.tags_item_name)
    TextView name;
    @BindView(R.id.tags_item_count)
    TextView counter;
    @BindView(R.id.tags_item_checkbox)
    AppCompatCheckBox checkBox;

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
    private final ScrollingItemDelegate scrollingItemDelegate;
    private boolean isEnabled;

    @Inject
    PublishSubject<TagViewHolder> stateChanges;
    @Inject
    PositionDelegate position;

    @Inject
    public TagItemHolder(@NonNull @TagRootView View itemView,
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
        if (isEnabled) callback.onTagClicked(position.get(), this);
    }


    @OnClick(R.id.tags_item_delete)
    public void onDeleteClicked() {
        if (isEnabled) callback.onDeleteClicked(position.get(), this);
    }

    @OnClick(R.id.tags_item_edit)
    public void onEditClicked() {
        if (isEnabled) callback.onEditClicked(position.get(), this);
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
    public void onAttached() {
        position.onAttached();
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
        CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList.valueOf(color));
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
