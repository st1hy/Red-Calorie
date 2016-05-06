package com.github.st1hy.countthemcalories.activities.ingredients.model;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.rx.RxDatabaseModel;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.JointIngredientTagDao;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Provider;

import dagger.Lazy;
import dagger.internal.DoubleCheckLazy;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.CursorQuery;
import rx.Observable;

public class IngredientTypesModel extends RxDatabaseModel<IngredientTemplate> {
    private final Lazy<IngredientTemplateDao> dao;

    public IngredientTypesModel(@NonNull Lazy<DaoSession> session) {
        super(session);
        this.dao = DoubleCheckLazy.create(new Provider<IngredientTemplateDao>() {

            @Override
            public IngredientTemplateDao get() {
                return session().getIngredientTemplateDao();
            }
        });
    }

    /**
     * Reads ingredient entity which is NOT cached or attached to dao into output
     */
    public void performReadEntity(@NonNull Cursor cursor, @NonNull IngredientTemplate output) {
        dao().readEntity(cursor, output, 0);
    }

    @NonNull
    @Override
    protected IngredientTemplate performGetById(long id) {
        IngredientTemplate template = dao().load(id);
        template.getTags();
        template.getChildIngredients();
        return template;
    }

    @NonNull
    private IngredientTemplateDao dao() {
        return dao.get();
    }

    @NonNull
    @Override
    protected IngredientTemplate performInsert(@NonNull IngredientTemplate data) {
        dao().insert(data);
        data.getChildIngredients();
        data.getTags();
        return data;
    }

    @NonNull
    public Observable<Cursor> addNewAndRefresh(@NonNull final IngredientTemplate data,
                                               @NonNull final Collection<Long> tagIds) {
        return fromDatabaseTask(insertNewRefreshCall(data, tagIds));
    }

    @NonNull
    private Callable<Cursor> insertNewRefreshCall(@NonNull final IngredientTemplate data,
                                                  @NonNull final Collection<Long> tagIds) {
        return new Callable<Cursor>() {
            @Override
            public Cursor call() throws Exception {
                IngredientTemplate template = performInsert(data);
                JointIngredientTagDao jointDao = session().getJointIngredientTagDao();
                TagDao tagDao = session().getTagDao();
                for (Long tagId : tagIds) {
                    Tag tag = tagDao.load(tagId);
                    JointIngredientTag join = new JointIngredientTag(null);
                    join.setTag(tag);
                    join.setIngredientType(template);
                    jointDao.insert(join);
                    tag.resetIngredientTypes();
                    tag.getIngredientTypes();
                }
                template.refresh();
                template.resetTags();
                template.getTags();
                return refresh().call();
            }
        };
    }

    @Override
    protected void performRemove(@NonNull IngredientTemplate data) {
        List<Ingredient> childIngredients = data.getChildIngredients();
        List<JointIngredientTag> tags = data.getTags();
        data.delete();
        for (Ingredient ingredient : childIngredients) {
            ingredient.delete();
        }
        for (JointIngredientTag join : tags) {
            join.getTag().getIngredientTypes().remove(join);
            join.delete();
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
                .orderAsc(IngredientTemplateDao.Properties.Name)
                .buildCursor();
    }

    @NonNull
    @Override
    protected CursorQuery filteredSortedByNameQuery() {
        return dao().queryBuilder()
                .where(IngredientTemplateDao.Properties.Name.like(""))
                .orderAsc(IngredientTemplateDao.Properties.Name)
                .buildCursor();
    }

    @Override
    protected long readKey(@NonNull Cursor cursor, int columnIndex) {
        return dao().readKey(cursor, columnIndex);
    }

    @NonNull
    @Override
    protected Property getKeyProperty() {
        return IngredientTemplateDao.Properties.Id;
    }

    @Override
    protected long getKey(IngredientTemplate ingredientTemplate) {
        return ingredientTemplate.getId();
    }
}
