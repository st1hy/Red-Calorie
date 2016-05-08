package com.github.st1hy.countthemcalories.activities.tags.model;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.rx.RxDatabaseModel;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;

import java.util.Collection;
import java.util.List;

import javax.inject.Provider;

import dagger.Lazy;
import dagger.internal.DoubleCheckLazy;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.CursorQuery;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

public class TagsModel extends RxDatabaseModel<Tag> {
    private final Lazy<TagDao> dao;

    public TagsModel(@NonNull Lazy<DaoSession> lazySession) {
        super(lazySession);
        this.dao = DoubleCheckLazy.create(new Provider<TagDao>() {
            @Override
            public TagDao get() {
                return session().getTagDao();
            }
        });
    }

    @Override
    public void performReadEntity(@NonNull Cursor cursor, @NonNull Tag output) {
        dao().readEntity(cursor, output, 0);
    }

    @NonNull
    @Override
    protected Tag performGetById(long id) {
        Tag tag = dao().load(id);
        tag.getIngredientTypes();
        return tag;
    }

    @NonNull
    @Override
    protected Tag performInsert(@NonNull Tag data) {
        dao().insert(data);
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
        dao().deleteAll();
    }


    @NonNull
    @Override
    protected CursorQuery allSortedByName() {
        return dao().queryBuilder()
                .orderAsc(TagDao.Properties.Name)
                .buildCursor();
    }

    @NonNull
    @Override
    protected CursorQuery filteredSortedByNameQuery() {
        return dao().queryBuilder()
                .where(TagDao.Properties.Name.like(""))
                .orderAsc(TagDao.Properties.Name)
                .buildCursor();
    }

    @NonNull
    @Override
    protected CursorQuery filteredExcludeSortedQuery(@NonNull final String partOfName,
                                                     @NonNull final Collection<Long> excludedIds) {
        QueryBuilder<Tag> builder = dao().queryBuilder();
        final WhereCondition notInList = TagDao.Properties.Id.notIn(excludedIds);
        if (!partOfName.isEmpty()) {
            builder = builder.where(TagDao.Properties.Name.like("%" + partOfName + "%"), notInList);
        } else {
            builder = builder.where(notInList);
        }
        return  builder
                .orderAsc(TagDao.Properties.Name)
                .buildCursor();
    }

    @Override
    protected long readKey(@NonNull Cursor cursor, int columnIndex) {
        return dao().readKey(cursor, columnIndex);
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

    @StringRes
    public int getNewTagDialogTitle() {
        return R.string.tags_new_tag_dialog;
    }

    private TagDao dao() {
        return dao.get();
    }

}
