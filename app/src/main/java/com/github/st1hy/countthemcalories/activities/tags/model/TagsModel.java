package com.github.st1hy.countthemcalories.activities.tags.model;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.rx.RxDatabaseModel;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;

import java.util.List;

import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.CursorQuery;

public class TagsModel extends RxDatabaseModel<Tag> {
    private final TagDao dao;

    public TagsModel(@NonNull DaoSession session) {
        super(session);
        this.dao = session.getTagDao();
    }

    @Override
    protected Tag performGetById(long id) {
        Tag tag = session.getTagDao().load(id);
        tag.getIngredientTypes();
        return tag;
    }

    @NonNull
    @Override
    protected Tag performInsert(@NonNull Tag data) {
        session.getTagDao().insert(data);
        return data;
    }

    @Override
    protected void performRemove(@NonNull Tag tag) {
        List<JointIngredientTag> joinTagIngredient = tag.getIngredientTypes();
        tag.delete();
        for (JointIngredientTag join : joinTagIngredient) {
            IngredientTemplate ingredient = join.getIngredientType();
            join.delete();
            ingredient.getTags().remove(join);
        }
    }

    @Override
    protected void performRemoveAll() {
        dao.deleteAll();
    }


    @NonNull
    @Override
    protected CursorQuery allSortedByName() {
        return dao.queryBuilder()
                .orderAsc(TagDao.Properties.Name)
                .buildCursor();
    }

    @NonNull
    @Override
    protected CursorQuery filteredSortedByNameQuery() {
        return dao.queryBuilder()
                .where(TagDao.Properties.Name.like(""))
                .orderAsc(TagDao.Properties.Name)
                .buildCursor();
    }

    @Override
    protected long readKey(@NonNull Cursor cursor, int columnIndex) {
        return dao.readKey(cursor, columnIndex);
    }

    @NonNull
    @Override
    protected Property getKeyProperty() {
        return TagDao.Properties.Id;
    }

    @Override
    protected long getKey(Tag tag) {
        return tag.getId();
    }

    public int getNewTagDialogTitle() {
        return R.string.tags_new_tag_dialog;
    }

    @Override
    protected Tag readEntity(@NonNull Cursor cursor) {
        return dao.readEntity(cursor, 0);
    }
}
