package com.github.st1hy.countthemcalories.activities.addingredient.fragment.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.adapter.callbacks.OnItemClicked;
import com.github.st1hy.countthemcalories.database.Tag;

import butterknife.BindView;
import butterknife.OnClick;

public class ItemTagViewHolder extends TagViewHolder {

    @BindView(R.id.add_ingredient_category_name)
    TextView categoryName;
    @BindView(R.id.add_ingredient_category_remove)
    ImageButton removeTag;
    private final OnItemClicked<Tag> onRemove;

    private Tag tag;

    public ItemTagViewHolder(@NonNull View itemView, @NonNull OnItemClicked<Tag> onRemove) {
        super(itemView);
        this.onRemove = onRemove;
    }

    public void setCategoryName(@NonNull String name) {
        categoryName.setText(name);
    }

    public void setTag(@NonNull Tag tag) {
        this.tag = tag;
    }

    @OnClick(R.id.add_ingredient_category_remove)
    public void onRemove() {
        if (tag != null) onRemove.onItemClicked(tag);
    }
}
