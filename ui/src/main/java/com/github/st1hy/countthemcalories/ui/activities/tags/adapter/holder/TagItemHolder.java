package com.github.st1hy.countthemcalories.ui.activities.tags.adapter.holder;

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
import com.github.st1hy.countthemcalories.ui.inject.tags.TagRootView;
import com.github.st1hy.countthemcalories.ui.activities.tags.model.ClickEvent;
import com.github.st1hy.countthemcalories.ui.activities.tags.model.TagsViewModel;
import com.github.st1hy.countthemcalories.ui.activities.tags.model.Type;
import com.github.st1hy.countthemcalories.ui.contract.Tag;
import com.github.st1hy.countthemcalories.ui.contract.TagFactory;
import com.github.st1hy.countthemcalories.ui.core.adapter.PositionDelegate;
import com.github.st1hy.countthemcalories.ui.core.rx.Transformers;
import com.github.st1hy.countthemcalories.ui.core.viewcontrol.ScrollingItemDelegate;
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
    private final Tag tag;
    private final ScrollingItemDelegate scrollingItemDelegate;
    private boolean isEnabled;

    @Inject
    PublishSubject<TagViewHolder> stateChanges;
    @Inject
    PublishSubject<ClickEvent> clickEvents;
    @Inject
    PositionDelegate position;
    @Inject
    TagFactory tagFactory;


    @Inject
    TagItemHolder(@NonNull @TagRootView View itemView,
                         @NonNull TagsViewModel viewModel) {
        super(itemView);
        this.viewModel = viewModel;
        ButterKnife.bind(this, itemView);
        scrollingItemDelegate = ScrollingItemDelegate.Builder.create()
                .setScrollView(scrollView)
                .setLeft(deleteFrame)
                .setCenter(content)
                .setRight(editFrame)
                .build();
        tag = tagFactory.newTag();
        tag.setTranslations(new I18n());
    }

    @OnClick(R.id.tag_item_button)
    void onClick() {
        onEvent(Type.OPEN);
    }

    @OnClick(R.id.tags_item_delete)
    void onDeleteClicked() {
        onEvent(Type.REMOVE);
    }

    @OnClick(R.id.tags_item_edit)
    void onEditClicked() {
        onEvent(Type.EDIT);
    }

    private void onEvent(Type type) {
        if (isEnabled) clickEvents.onNext(new ClickEvent(type, position.get(), this));
    }

    @NonNull
    public Tag getReusableTag() {
        return tag;
    }

    public void bind(int position, @NonNull Tag tag) {
        this.position.set(position);
        name.setText(tag.getDisplayName());
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
