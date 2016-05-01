package com.github.st1hy.countthemcalories.activities.tags.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TagViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tags_item_name)
    TextView name;

    public TagViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void setName(@NonNull String name) {
        this.name.setText(name);
    }
}
