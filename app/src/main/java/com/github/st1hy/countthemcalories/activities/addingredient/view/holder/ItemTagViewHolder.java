package com.github.st1hy.countthemcalories.activities.addingredient.view.holder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.adapter.callbacks.OnItemClicked;

import butterknife.BindView;

public class ItemTagViewHolder extends TagViewHolder implements View.OnClickListener {

    @BindView(R.id.add_ingredient_category_name)
    TextView categoryName;
    @BindView(R.id.add_ingredient_category_remove)
    ImageButton removeTag;
    int position;
    final OnItemClicked<Integer> onRemove;

    public ItemTagViewHolder(@NonNull View itemView, @NonNull OnItemClicked<Integer> onRemove) {
        super(itemView);
        this.onRemove = onRemove;
        removeTag.setOnClickListener(this);
    }

    public void setCategoryName(@NonNull String name) {
        categoryName.setText(name);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void onClick(View v) {
        onRemove.onItemClicked(position);
    }
}
