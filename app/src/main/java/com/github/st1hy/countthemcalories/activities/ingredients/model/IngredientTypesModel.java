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

import java.util.List;
import java.util.concurrent.Callable;

import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.CursorQuery;
import rx.Observable;

public class IngredientTypesModel extends RxDatabaseModel<IngredientTemplate> {
    private final IngredientTemplateDao dao;

    public IngredientTypesModel(@NonNull DaoSession session) {
        super(session);
        this.dao = session.getIngredientTemplateDao();
    }

    @Override
    protected IngredientTemplate performGetById(long id) {
        IngredientTemplate template = dao.load(id);
        template.getTags();
        template.getChildIngredients();
        return template;
    }

    @NonNull
    @Override
    protected IngredientTemplate performInsert(@NonNull IngredientTemplate data) {
        dao.insert(data);
        data.getChildIngredients();
        data.getTags();
        return data;
    }

    @NonNull
    public Observable<Cursor> addNewAndRefresh(@NonNull final IngredientTemplate data,
                                               @NonNull final List<Tag> all) {
        return fromDatabaseTask(insertNewRefreshCall(data, all));
    }

    @NonNull
    private Callable<Cursor> insertNewRefreshCall(@NonNull final IngredientTemplate data,
                                                                    @NonNull final List<Tag> all) {
        return new Callable<Cursor>() {
            @Override
            public Cursor call() throws Exception {
                IngredientTemplate template = performInsert(data);
                List<JointIngredientTag> ingredientJoins = template.getTags();
                JointIngredientTagDao jointDao = session.getJointIngredientTagDao();
                for (Tag tag : all) {
                    List<JointIngredientTag> tagJoins = tag.getIngredientTypes();
                    JointIngredientTag join = new JointIngredientTag(null);
                    join.setTag(tag);
                    join.setIngredientType(template);
                    jointDao.insert(join);
                    tagJoins.add(join);
                    ingredientJoins.add(join);
                }
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
        session.getIngredientTemplateDao().deleteAll();
    }

    @NonNull
    @Override
    protected CursorQuery allSortedByName() {
        return dao.queryBuilder()
                .orderAsc(IngredientTemplateDao.Properties.Name)
                .buildCursor();
    }

    @NonNull
    @Override
    protected CursorQuery filteredSortedByNameQuery() {
        return dao.queryBuilder()
                .where(IngredientTemplateDao.Properties.Name.like(""))
                .orderAsc(IngredientTemplateDao.Properties.Name)
                .buildCursor();
    }

    @Override
    protected long readKey(@NonNull Cursor cursor, int columnIndex) {
        return dao.readKey(cursor, columnIndex);
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

    @Override
    protected IngredientTemplate readEntity(@NonNull Cursor cursor) {
        return dao.readEntity(cursor, 0);
    }
}
