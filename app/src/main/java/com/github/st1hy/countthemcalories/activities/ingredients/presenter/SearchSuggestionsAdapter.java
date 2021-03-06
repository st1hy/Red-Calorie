package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Filter;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.activities.tags.fragment.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.database.I18n;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.quantifier.context.ActivityContext;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@PerActivity
public class SearchSuggestionsAdapter extends ResourceCursorAdapter {

    @Inject
    RxTagsDatabaseModel tagsDatabaseModel;

    private Filter filter;
    private final Tag reusableTag;

    @Inject
    public SearchSuggestionsAdapter(@NonNull @ActivityContext Context context) {
        super(context, android.R.layout.simple_list_item_1, null, false);
        reusableTag = new Tag();
        reusableTag.setTranslations(new I18n());
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
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        viewHolder.name.setText(readTagNameFromCursor(cursor));
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
        tagsDatabaseModel.performReadEntity(cursor, reusableTag);
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

    static class ViewHolder {
        @BindView(android.R.id.text1)
        TextView name;

        public ViewHolder(View root) {
            ButterKnife.bind(this, root);
        }
    }
}
