package com.github.st1hy.countthemcalories.ui.activities.ingredients.presenter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Filter;
import android.widget.ResourceCursorAdapter;

import com.github.st1hy.countthemcalories.ui.contract.Tag;
import com.github.st1hy.countthemcalories.ui.contract.TagFactory;
import com.github.st1hy.countthemcalories.ui.contract.TagsRepo;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.context.ActivityContext;

import javax.inject.Inject;

@PerActivity
public class SearchSuggestionsAdapter extends ResourceCursorAdapter {

    @Inject
    TagsRepo repo;
    @Inject
    TagFactory tagFactory;

    private Filter filter;
    private final Tag reusableTag;

    @Inject
    public SearchSuggestionsAdapter(@NonNull @ActivityContext Context context) {
        super(context, android.R.layout.simple_list_item_1, null, false);
        reusableTag = tagFactory.newTag();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FakeFilter();
        }
        return filter;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        SuggestionViewHolder viewHolder = (SuggestionViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SuggestionViewHolder(view);
            view.setTag(viewHolder);
        }
        viewHolder.getName().setText(readTagNameFromCursor(cursor));
    }

    /**
     * While internally adapter uses cursor, from outside perspective this adapter
     * should be adapter of String, TokenSearchView assumes object it type of TokenSearchView
     * template parameter when creating chips
     */
    @Override
    public String getItem(int position) {
        Cursor cursor = (Cursor) super.getItem(position);
        cursor.moveToPosition(position);
        return readTagNameFromCursor(cursor);
    }

    private String readTagNameFromCursor(@NonNull Cursor cursor) {
        repo.readEntry(cursor, reusableTag);
        return reusableTag.getDisplayName();
    }

    private static class FakeFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
        }
    }

}
