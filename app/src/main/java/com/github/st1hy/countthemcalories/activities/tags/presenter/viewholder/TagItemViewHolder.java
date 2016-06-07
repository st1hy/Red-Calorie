package com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.adapter.PositionDelegate;
import com.github.st1hy.countthemcalories.core.viewcontrol.ScrollingItemDelegate;
import com.github.st1hy.countthemcalories.database.Tag;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TagItemViewHolder extends TagViewHolder {

    @BindView(R.id.tags_item_name)
    TextView name;

    @BindView(R.id.tags_item_scrollview)
    HorizontalScrollView scrollView;

    @BindView(R.id.tags_item_delete_frame)
    View deleteFrame;
    @BindView(R.id.tags_item_edit_frame)
    View editFrame;
    @BindView(R.id.tags_item_content)
    View content;

    final OnTagInteraction callback;
    private final Tag tag = new Tag();
    private final PositionDelegate position = new PositionDelegate();
    private final ScrollingItemDelegate scrollingItemDelegate;

    public TagItemViewHolder(@NonNull View itemView, @NonNull OnTagInteraction callback) {
        super(itemView);
        this.callback = callback;
        ButterKnife.bind(this, itemView);
        scrollingItemDelegate = ScrollingItemDelegate.Builder.create()
                .setScrollView(scrollView)
                .setLeft(deleteFrame)
                .setCenter(content)
                .setRight(editFrame)
                .build();
    }

    @OnClick(R.id.tags_item_delete)
    public void onDeleteClicked() {
        callback.onDeleteClicked(position.get(), tag);
    }

    @OnClick(R.id.tags_item_edit)
    public void onEditClicked() {
        callback.onEditClicked(position.get(), tag);
    }

    @OnClick(R.id.tag_item_button)
    public void onClick() {
        callback.onTagClicked(position.get(), tag);
    }

    @NonNull
    public Tag getReusableTag() {
        return tag;
    }

    public void bind(int position, @NonNull Tag tag) {
        this.position.set(position);
        name.setText(tag.getName());
    }

    public void onAttached() {
        position.onAttached(callback.getEvents());
        scrollingItemDelegate.onAttached();
    }

    public void onDetached() {
        position.onDetached();
        scrollingItemDelegate.onDetached();
    }

    public void fillParent(@NonNull ViewGroup parent) {
        scrollingItemDelegate.fillParent(parent);
    }
}
