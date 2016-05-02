package com.github.st1hy.countthemcalories.activities.tags.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.presenter.OnItemInteraction;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TagViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

    @Bind(R.id.tags_item_name)
    TextView name;
    int position;
    final OnItemInteraction listener;

    public TagViewHolder(@NonNull View itemView, @NonNull OnItemInteraction listener) {
        super(itemView);
        this.listener = listener;
        ButterKnife.bind(this, itemView);
        itemView.setOnLongClickListener(this);
        itemView.setOnClickListener(this);
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
