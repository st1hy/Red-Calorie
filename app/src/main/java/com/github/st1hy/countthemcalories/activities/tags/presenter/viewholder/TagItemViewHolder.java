package com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.adapter.PositionDelegate;
import com.github.st1hy.countthemcalories.database.Tag;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class TagItemViewHolder extends TagViewHolder {

    @BindView(R.id.tags_item_name)
    TextView name;

    final OnTagInteraction callback;
    private final Tag tag = new Tag();
    private final PositionDelegate position = new PositionDelegate();

    public TagItemViewHolder(@NonNull View itemView, @NonNull OnTagInteraction callback) {
        super(itemView);
        this.callback = callback;
        ButterKnife.bind(this, itemView);
    }

    @OnLongClick(R.id.tag_button)
    public boolean onLongClick() {
        callback.onTagLongClicked(position.get(), tag);
        return true;
    }

    @OnClick(R.id.tag_button)
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
    }

    public void onDetached() {
        position.onDetached();
    }
}
