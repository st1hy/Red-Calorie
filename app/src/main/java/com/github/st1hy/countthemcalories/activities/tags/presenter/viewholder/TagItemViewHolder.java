package com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.database.Tag;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TagItemViewHolder extends TagViewHolder implements View.OnLongClickListener,
        View.OnClickListener {

    @BindView(R.id.tags_item_name)
    TextView name;
    @BindView(R.id.tag_button)
    View button;

    final OnTagInteraction listener;
    int position;

    private final Tag tag = new Tag();

    public TagItemViewHolder(@NonNull View itemView, @NonNull OnTagInteraction listener) {
        super(itemView);
        this.listener = listener;
        ButterKnife.bind(this, itemView);
        button.setOnLongClickListener(this);
        button.setOnClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        if (tag != null) {
            listener.onItemLongClicked(position, tag);
            return true;
        } else return false;
    }

    @Override
    public void onClick(View v) {
        if (tag != null) {
            listener.onItemClicked(position, tag);
        }
    }

    @NonNull
    public Tag getReusableTag() {
        return tag;
    }

    public void bind(int position, @NonNull Tag tag) {
        this.position = position;
        name.setText(tag.getName());
    }
}
