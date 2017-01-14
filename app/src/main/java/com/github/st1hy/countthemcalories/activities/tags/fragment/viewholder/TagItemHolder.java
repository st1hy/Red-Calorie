package com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.adapter.PositionDelegate;
import com.github.st1hy.countthemcalories.database.Tag;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TagItemHolder extends TagViewHolder {

    @BindView(R.id.tags_item_name)
    TextView name;

    private final OnTagInteraction callback;
    private final Tag tag = new Tag();
    private final PositionDelegate position = new PositionDelegate();

    public TagItemHolder(@NonNull View itemView, @NonNull OnTagInteraction callback) {
        super(itemView);
        this.callback = callback;
        ButterKnife.bind(this, itemView);
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

    @Override
    public void onAttached() {
        position.onAttached(callback.getEvents());
    }

    @Override
    public void onDetached() {
        position.onDetached();
    }
}
