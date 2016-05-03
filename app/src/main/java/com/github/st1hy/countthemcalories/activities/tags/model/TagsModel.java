package com.github.st1hy.countthemcalories.activities.tags.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.rx.RxDatabaseModel;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.dao.query.Query;

public class TagsModel extends RxDatabaseModel<Tag> {

    @Inject
    public TagsModel(@NonNull DaoSession session) {
        super(session);
    }

    @Override
    protected Tag performGetById(long id) {
        return session.getTagDao().load(id);
    }

    @NonNull
    @Override
    protected List<Tag> performQuery(@NonNull Query<Tag> query) {
        List<Tag> list = query.list();
        for (Tag tag : list) {
            tag.getIngredientTypes();
        }
        return list;
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
        session.getTagDao().deleteAll();
    }

    @NonNull
    @Override
    protected Query<Tag> allSortedByName() {
        return session.getTagDao()
                .queryBuilder()
                .orderAsc(TagDao.Properties.Name)
                .build();
    }

    @NonNull
    @Override
    protected Query<Tag> filteredSortedByNameQuery() {
        return session.getTagDao()
                .queryBuilder()
                .where(TagDao.Properties.Name.like(""))
                .orderAsc(TagDao.Properties.Name)
                .build();
    }

    @Override
    protected boolean equal(@NonNull Tag a, @NonNull Tag b) {
        return a.getId().equals(b.getId());
    }

    public int getNewTagDialogTitle() {
        return R.string.tags_new_tag_dialog;
    }
}
