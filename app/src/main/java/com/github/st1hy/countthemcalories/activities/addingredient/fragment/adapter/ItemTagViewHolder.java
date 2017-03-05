package com.github.st1hy.countthemcalories.activities.addingredient.fragment.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.database.Tag;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

class ItemTagViewHolder extends TagViewHolder {

    @BindView(R.id.add_ingredient_category_name)
    TextView categoryName;
    private final Action1<Tag> onRemove;

    private Tag tag;

    ItemTagViewHolder(@NonNull View itemView, @NonNull Action1<Tag> onRemove) {
        super(itemView);
        this.onRemove = onRemove;
    }

    void setCategoryName(@NonNull String name) {
        categoryName.setText(name);
    }

    void setTag(@NonNull Tag tag) {
        this.tag = tag;
    }

    @OnClick(R.id.add_ingredient_category_remove)
    void onRemove() {
        if (tag != null) onRemove.call(tag);
    }
}
