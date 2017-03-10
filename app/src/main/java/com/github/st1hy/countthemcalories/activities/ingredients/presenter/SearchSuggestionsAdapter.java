package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.widget.Filter;
import android.widget.SimpleCursorAdapter;

import com.github.st1hy.countthemcalories.database.TagDao;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.quantifier.context.ActivityContext;

import javax.inject.Inject;

@PerActivity
public class SearchSuggestionsAdapter extends SimpleCursorAdapter {

    private static final String COLUMN = TagDao.Properties.Name.columnName;

    private Filter filter;

    @Inject
    public SearchSuggestionsAdapter(@NonNull @ActivityContext Context context) {
        super(context,
                android.R.layout.simple_list_item_1,
                null,
                new String[]{COLUMN},
                new int[]{android.R.id.text1},
                0);
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FakeFilter();
        }
        return filter;
    }

    /**
     * While internally adapter uses cursor, from outside perspective this adapter
     * should be adapter of String, TokenSearchView assumes object it type of TokenSearchView
     * template parameter when creating chips
     */
    @Override
    public Object getItem(int position) {
        Cursor cursor = (Cursor) super.getItem(position);
        cursor.moveToPosition(position);
        return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN));
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
