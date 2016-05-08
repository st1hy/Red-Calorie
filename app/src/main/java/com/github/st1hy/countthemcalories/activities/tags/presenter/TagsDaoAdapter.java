package com.github.st1hy.countthemcalories.activities.tags.presenter;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsActivityModel;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsModel;
import com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder.EmptySpaceViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder.TagItemViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder.TagViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsView;
import com.github.st1hy.countthemcalories.core.adapter.callbacks.OnItemInteraction;
import com.github.st1hy.countthemcalories.core.adapter.RxDaoRecyclerAdapter;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Tag;
import com.google.common.base.Strings;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class TagsDaoAdapter extends RxDaoRecyclerAdapter<TagViewHolder, Tag>
        implements OnItemInteraction<Tag> {

    static final int bottomSpaceItem = 1;
    static final int item_layout = R.layout.tags_item;
    static final int item_bottom_space_layout = R.layout.tags_item_bottom_space;

    final TagsView view;
    final TagsModel model;
    final TagsActivityModel activityModel;

    @Inject
    public TagsDaoAdapter(@NonNull TagsView view,
                          @NonNull TagsModel model,
                          @NonNull TagsActivityModel activityModel) {
        super(model);
        this.view = view;
        this.model = model;
        this.activityModel = activityModel;
    }


    @Override
    public void onStart() {
        super.onStart();
        onAddTagClicked(view.getOnAddTagClickedObservable());
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getDaoItemCount()) {
            return item_layout;
        } else {
            return item_bottom_space_layout;
        }
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, null);
        if (viewType == item_layout) {
            return new TagItemViewHolder(view, this);
        } else {
            return new EmptySpaceViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        if (holder instanceof TagItemViewHolder) {
            onBindToTagHolder((TagItemViewHolder) holder, position);
        }
    }

    private void onBindToTagHolder(@NonNull TagItemViewHolder holder, int position) {
        Cursor cursor = getCursor();
        if (cursor != null) {
            cursor.moveToPosition(position);
            Tag tag = holder.getReusableTag();
            model.performReadEntity(cursor, tag);
            holder.bind(tag);
        } else {
            Timber.w("Cursor closed duding binding views.");
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + bottomSpaceItem;
    }

    @Override
    public void onItemClicked(@NonNull Tag tag) {
        if (activityModel.isInSelectMode()) {
            view.setResultAndReturn(tag.getId(), tag.getName());
        }
    }

    @Override
    public void onItemLongClicked(@NonNull Tag tag) {
        subscribe(view.showRemoveTagDialog().subscribe(deleteTag(tag)));
    }

    @Override
    protected void onCursorUpdate(@NonNull Cursor cursor) {
        super.onCursorUpdate(cursor);
        view.setNoTagsButtonVisibility(Visibility.of(cursor.getCount() == 0));
    }

    void onAddTagClicked(@NonNull Observable<Void> clicks) {
        subscribeToCursor(clicks
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Timber.v("Add tag clicked");
                    }
                })
                .flatMap(showNewTagDialog())
                .map(trim())
                .filter(notEmpty())
                .flatMap(addTagRefresh())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Cursor>() {
                    @Override
                    public void call(Cursor cursor) {
                        view.scrollToPosition(cursor.getPosition());
                    }
                })
                .retry());
    }

    @NonNull
    Func1<String, Observable<Cursor>> addTagRefresh() {
        return new Func1<String, Observable<Cursor>>() {
            @Override
            public Observable<Cursor> call(String tagName) {
                return model.addNewAndRefresh(new Tag(null, tagName));
            }
        };
    }

    @NonNull
    Func1<Void, Observable<String>> showNewTagDialog() {
        return new Func1<Void, Observable<String>>() {
            @Override
            public Observable<String> call(Void aVoid) {
                return view.showEditTextDialog(model.getNewTagDialogTitle());
            }
        };
    }

    @NonNull
    Func1<String, Boolean> notEmpty() {
        return new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return !Strings.isNullOrEmpty(s);
            }
        };
    }

    @NonNull
    Func1<String, String> trim() {
        return new Func1<String, String>() {
            @Override
            public String call(String s) {
                return s.trim();
            }
        };
    }

    @NonNull
    Action1<Void> deleteTag(final Tag tag) {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                subscribeToCursor(model.removeAndRefresh(tag.getId()));
            }
        };
    }

}
