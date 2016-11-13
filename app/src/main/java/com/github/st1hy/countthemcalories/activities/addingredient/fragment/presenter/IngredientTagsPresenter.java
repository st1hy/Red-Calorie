package com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.IngredientTagsModel;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.view.AddIngredientView;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.viewholder.AddNewTagViewHolder;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.viewholder.ItemTagViewHolder;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.viewholder.TagViewHolder;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerViewNotifier;
import com.github.st1hy.countthemcalories.core.adapter.callbacks.OnItemClicked;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.database.Tag;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class IngredientTagsPresenter implements OnItemClicked<Tag>, RecyclerAdapterWrapper<TagViewHolder> {

    private static final int TAG = R.layout.add_ingredient_tag;
    private static final int ADD_TAG = R.layout.add_ingredient_add_tag;
    private static final int ADD_CATEGORY_FIELDS_SIZE = 1;

    @NonNull
    private final IngredientTagsModel model;
    @NonNull
    private final AddIngredientView view;

    private RecyclerViewNotifier notifier;

    @Inject
    public IngredientTagsPresenter(@NonNull IngredientTagsModel model, @NonNull AddIngredientView view) {
        this.model = model;
        this.view = view;
    }

    @NonNull
    public void setNotifier(@NonNull RecyclerViewNotifier notifier) {
        this.notifier = notifier;
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        switch (viewType) {
            case TAG:
                return new ItemTagViewHolder(view, this);
            case ADD_TAG:
                return new AddNewTagViewHolder(view);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TAG:
                onBindTag((ItemTagViewHolder) holder, position);
                break;
            case ADD_TAG:
                onBindAddTag((AddNewTagViewHolder) holder);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private void onBindTag(@NonNull ItemTagViewHolder viewHolder, int position) {
        Tag tag = model.getTagAt(position);
        viewHolder.setCategoryName(tag.getName());
        viewHolder.setTag(tag);
    }

    private void onBindAddTag(@NonNull AddNewTagViewHolder holder) {
        holder.addNewObservable().flatMap(new Func1<Void, Observable<Tag>>() {
            @Override
            public Observable<Tag> call(Void aVoid) {
                return view.selectTag(model.getTagNames());
            }
        }).subscribe(new SimpleSubscriber<Tag>() {
            @Override
            public void onNext(Tag tag) {
                int position = model.addTag(tag);
                notifier.notifyItemInserted(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return model.getSize() + ADD_CATEGORY_FIELDS_SIZE;
    }

    private int getItemViewType(int position) {
        if (position < model.getSize()) {
            return TAG;
        } else {
            return ADD_TAG;
        }
    }

    @Override
    public void onItemClicked(@NonNull Tag tag) {
        int position = model.remove(tag);
        notifier.notifyItemRemoved(position);
    }
}
