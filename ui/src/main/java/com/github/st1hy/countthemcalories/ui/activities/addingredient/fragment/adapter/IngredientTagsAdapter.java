package com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.model.IngredientTagsModel;
import com.github.st1hy.countthemcalories.ui.contract.Tag;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;

import javax.inject.Inject;

import rx.functions.Action1;

@PerFragment
public class IngredientTagsAdapter extends RecyclerView.Adapter<TagViewHolder> {

    private static final int TAG = R.layout.add_ingredient_tag;

    @NonNull
    private final IngredientTagsModel model;

    private final Action1<Tag> onRemoveAction = new Action1<Tag>() {
        @Override
        public void call(Tag tag) {
            int position = model.remove(tag);
            notifyItemRemoved(position);
        }
    };

    @Inject
    IngredientTagsAdapter(@NonNull IngredientTagsModel model) {
        this.model = model;
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        if (viewType == TAG) {
            return new ItemTagViewHolder(view, onRemoveAction);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TAG) {
            onBindTag((ItemTagViewHolder) holder, position);

        } else {
            throw new IllegalArgumentException();
        }
    }

    private void onBindTag(@NonNull ItemTagViewHolder viewHolder, int position) {
        Tag tag = model.getTagAt(position);
        viewHolder.setCategoryName(tag.getDisplayName());
        viewHolder.setTag(tag);
    }

    @Override
    public int getItemCount() {
        return model.getSize();
    }

    public int getItemViewType(int position) {
        if (position < model.getSize()) {
            return TAG;
        } else {
            throw new IllegalArgumentException();
        }
    }

}
