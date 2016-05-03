package com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.presenter.OnItemInteraction;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TagItemViewHolder extends TagViewHolder implements View.OnLongClickListener, View.OnClickListener {

    @Bind(R.id.tags_item_name)
    TextView name;
    @Bind(R.id.tag_button)
    View button;
    int position;
    final OnItemInteraction listener;

    public TagItemViewHolder(@NonNull View itemView, @NonNull OnItemInteraction listener) {
        super(itemView);
        this.listener = listener;
        ButterKnife.bind(this, itemView);
        button.setOnLongClickListener(this);
        button.setOnClickListener(this);
    }

    public void setName(@NonNull String name) {
        this.name.setText(name);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public boolean onLongClick(View v) {
        listener.onItemLongClicked(position);
        return true;
    }

    @Override
    public void onClick(View v) {
        listener.onItemClicked(position);
    }
}
