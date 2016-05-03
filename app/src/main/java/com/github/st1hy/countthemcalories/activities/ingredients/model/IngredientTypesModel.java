package com.github.st1hy.countthemcalories.activities.ingredients.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.rx.RxDatabaseModel;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import de.greenrobot.dao.query.Query;
import rx.Observable;

public class IngredientTypesModel extends RxDatabaseModel<IngredientTemplate> {

    @Inject
    public IngredientTypesModel(@NonNull DaoSession session) {
        super(session);
    }

    @Override
    protected IngredientTemplate performGetById(long id) {
        return session.getIngredientTemplateDao().load(id);
    }

    @NonNull
    @Override
    protected List<IngredientTemplate> performQuery(@NonNull Query<IngredientTemplate> query) {
        List<IngredientTemplate> list = query.list();
        for (IngredientTemplate item : list) {
            item.getTags();
            item.getChildIngredients();
        }
        return list;
    }

    @NonNull
    @Override
    protected IngredientTemplate performInsert(@NonNull IngredientTemplate data) {
        session.getIngredientTemplateDao().insert(data);
        data.getChildIngredients();
        data.getTags();
        return data;
    }

    @NonNull
    public Observable<List<IngredientTemplate>> addNewAndRefresh(@NonNull final IngredientTemplate data,
                                                                 @NonNull final List<Tag> all) {
        return fromDatabaseTask(insertNewRefreshCall(data, all));
    }

    @NonNull
    private Callable<List<IngredientTemplate>> insertNewRefreshCall(@NonNull final IngredientTemplate data,
                                                                    @NonNull final List<Tag> all) {
        return new Callable<List<IngredientTemplate>>() {
            @Override
            public List<IngredientTemplate> call() throws Exception {
                IngredientTemplate template = performInsert(data);
                List<JointIngredientTag> ingredientJoins = template.getTags();
                for (Tag tag : all) {
                    List<JointIngredientTag> tagJoins = tag.getIngredientTypes();
                    JointIngredientTag join = new JointIngredientTag(null);
                    join.setTag(tag);
                    join.setIngredientType(template);
                    session.getJointIngredientTagDao().insert(join);
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

    @Override
    protected boolean equal(@NonNull IngredientTemplate a, @NonNull IngredientTemplate b) {
        return a.getId().equals(b.getId());
    }


    @NonNull
    @Override
    protected Query<IngredientTemplate> allSortedByName() {
        return session.getIngredientTemplateDao()
                .queryBuilder()
                .orderAsc(IngredientTemplateDao.Properties.Name)
                .build();
    }

    @NonNull
    @Override
    protected Query<IngredientTemplate> filteredSortedByNameQuery() {
        return session.getIngredientTemplateDao()
                .queryBuilder()
                .where(IngredientTemplateDao.Properties.Name.like(""))
                .orderAsc(IngredientTemplateDao.Properties.Name)
                .build();
    }
}
