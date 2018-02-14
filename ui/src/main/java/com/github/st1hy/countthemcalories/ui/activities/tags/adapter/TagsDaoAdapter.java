package com.github.st1hy.countthemcalories.ui.activities.tags.adapter;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.ui.activities.tags.adapter.holder.TagItemHolder;
import com.github.st1hy.countthemcalories.ui.activities.tags.adapter.holder.TagViewHolder;
import com.github.st1hy.countthemcalories.ui.activities.tags.model.ColorGenerator;
import com.github.st1hy.countthemcalories.ui.activities.tags.model.TagsFragmentModel;
import com.github.st1hy.countthemcalories.ui.contract.Tag;
import com.github.st1hy.countthemcalories.ui.contract.TagsRepo;
import com.github.st1hy.countthemcalories.ui.core.adapter.CursorRecyclerViewAdapter;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.tags.TagComponent;
import com.github.st1hy.countthemcalories.ui.inject.tags.TagComponentFactory;
import com.github.st1hy.countthemcalories.ui.inject.tags.TagModule;
import com.l4digital.fastscroll.FastScroller;

import javax.inject.Inject;

import timber.log.Timber;

@PerFragment
public class TagsDaoAdapter extends CursorRecyclerViewAdapter<TagViewHolder> implements
        FastScroller.SectionIndexer {

    private static final int item_layout = R.layout.tags_item_scrolling;
    private static final int space_bottom = R.layout.list_item_bottom;
    private static final int space_top = R.layout.list_item_top;
    private static final int TOP_ITEM_PADDING = 1;
    private static final int BOTTOM_ITEM_PADDING = 1;
    private static final int ADDITIONAL_ITEMS = BOTTOM_ITEM_PADDING + TOP_ITEM_PADDING;


    @NonNull
    private final TagsFragmentModel fragmentModel;
    @Inject
    ColorGenerator colorGenerator;
    @Inject
    TagComponentFactory tagComponentFactory;
    @Inject
    TagsRepo tagsRepo;

    @Inject
    TagsDaoAdapter(@NonNull TagsFragmentModel fragmentModel) {
        this.fragmentModel = fragmentModel;
    }

    public void onStart() {
    }

    public void onStop() {
        closeCursor(true);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < TOP_ITEM_PADDING) {
            return space_top;
        } else if (position < getDaoItemCount() + TOP_ITEM_PADDING) {
            return item_layout;
        } else {
            return space_bottom;
        }
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        TagComponent tagComponent = tagComponentFactory.newTagComponent(new TagModule(view));
        if (viewType == item_layout) {
            TagItemHolder itemHolder = tagComponent.tagItemHolder();
            itemHolder.fillParent(parent);
            return itemHolder;
        } else {
            return tagComponent.tagSpaceHolder();
        }
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        if (holder instanceof TagItemHolder) {
            onBindItemHolder((TagItemHolder) holder, position);
        }
    }

    private void onBindItemHolder(TagItemHolder holder, int position) {
        Cursor cursor = getCursor();
        if (cursor != null) {
            cursor.moveToPosition(positionInCursor(position));
            Tag tag = holder.getReusableTag();
            holder.setEnabled(true);
            tagsRepo.readEntry(cursor, tag);
            holder.bind(position, tag);
            holder.setSelectable(fragmentModel.isInSelectMode());
            holder.setChecked(fragmentModel.isSelected(tag));
            if (!fragmentModel.isInSelectMode()) {
                holder.setCheckedTint(colorGenerator.getColorFor(tag.getId()));
            }
        } else {
            Timber.w("Cursor closed duding binding views.");
        }
    }

    @Override
    public void onViewAttachedToWindow(TagViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.onAttached();
    }

    @Override
    public void onViewDetachedFromWindow(TagViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.onDetached();
    }

    @Override
    public int getItemCount() {
        int daoItemCount = getDaoItemCount();
        return daoItemCount > 0 ? daoItemCount + ADDITIONAL_ITEMS : 0;
    }

    public void onCursorUpdate(@NonNull Cursor cursor) {
        super.onCursorUpdate(cursor);
    }

    @Override
    public String getSectionText(int position) {
        Cursor cursor = getCursor();
        if (cursor != null) {
            position = positionInCursor(position);
            if (position >= getDaoItemCount()) {
                position = getDaoItemCount() - 1;
            } else if (position < 0) {
                position = 0;
            }
            cursor.moveToPosition(position);
            String string = tagsRepo.readName(cursor);
            //String string = cursor.getString(cursor.getColumnIndexOrThrow(TagDao.Properties.Name.columnName));
            return string.substring(0, 1);
        } else {
            return "";
        }
    }

    private static int positionInCursor(int positionInAdapter) {
        return positionInAdapter - TOP_ITEM_PADDING;
    }

    private static int positionInAdapter(int positionInCursor) {
        return positionInCursor + TOP_ITEM_PADDING;
    }

    public void onNewItemPositionInCursor(int position, int newPosition) {
        if (newPosition != -1) {
            newPosition = positionInAdapter(newPosition);
            notifyListenersItemRemove(position);
            if (newPosition != position) notifyItemRemoved(position);
            notifyListenersItemInserted(newPosition);
            if (newPosition != position) {
                notifyItemInserted(newPosition);
            } else {
                notifyItemChanged(position);
            }
        } else {
            notifyDataSetChanged();
        }
    }

    public void onPositionRemoved(int positionInAdapter) {
        int previousCursorSize = getDaoItemCount();
        if (positionInAdapter != -1) {
            notifyListenersItemRemove(positionInAdapter);
            if (previousCursorSize > 1) {
                notifyItemRemoved(positionInAdapter);
            } else {
                notifyDataSetChanged();
            }
        } else {
            notifyDataSetChanged();
        }
    }

    public boolean onPositionAdded(int newItemPosition) {
        int previousCursorSize = getDaoItemCount();
        if (newItemPosition != -1) {
            newItemPosition = positionInAdapter(newItemPosition);
            notifyListenersItemInserted(newItemPosition);
            if (previousCursorSize > 0) {
                notifyItemInserted(newItemPosition);
                return true;
            } else {
                notifyDataSetChanged();
            }
        } else {
            notifyDataSetChanged();
        }
        return false;
    }
}
