package com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.callbacks.OnItemInteraction;
import com.github.st1hy.countthemcalories.database.Tag;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TagItemViewHolder extends TagViewHolder implements View.OnLongClickListener, View.OnClickListener {

    @Bind(R.id.tags_item_name)
    TextView name;
    @Bind(R.id.tag_button)
    View button;

    final OnItemInteraction<Tag> listener;
    private final Tag tag = new Tag();

    public TagItemViewHolder(@NonNull View itemView, @NonNull OnItemInteraction<Tag> listener) {
        super(itemView);
        this.listener = listener;
        ButterKnife.bind(this, itemView);
        button.setOnLongClickListener(this);
        button.setOnClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        if (tag != null) {
            listener.onItemLongClicked(tag);
            return true;
        } else return false;
    }

    @Override
    public void onClick(View v) {
        if (tag != null) {
            listener.onItemClicked(tag);
        }
    }

    @NonNull
    public Tag getReusableTag() {
        return tag;
    }

    public void bind(@NonNull Tag tag) {
        name.setText(tag.getName());
    }
}
