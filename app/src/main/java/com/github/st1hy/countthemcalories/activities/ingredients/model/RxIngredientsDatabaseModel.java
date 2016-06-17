package com.github.st1hy.countthemcalories.activities.ingredients.model;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.core.rx.RxDatabaseModel;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao.Properties;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.JointIngredientTagDao;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Provider;

import dagger.Lazy;
import dagger.internal.DoubleCheckLazy;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.CursorQuery;
import de.greenrobot.dao.query.Join;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;

public class RxIngredientsDatabaseModel extends RxDatabaseModel<IngredientTemplate> {
    private final Lazy<IngredientTemplateDao> dao;
    private Collection<String> lastTagsFilter = Collections.emptyList();

    public RxIngredientsDatabaseModel(@NonNull Lazy<DaoSession> session) {
        super(session);
        this.dao = DoubleCheckLazy.create(new Provider<IngredientTemplateDao>() {

            @Override
            public IngredientTemplateDao get() {
                return session().getIngredientTemplateDao();
            }
        });
    }

    public Observable<Cursor> getAllFilteredBy(@NonNull String name, @NonNull List<String> tags) {
        return fromDatabaseTask(filteredBy(name, tags));
    }

    @NonNull
    private Callable<Cursor> filteredBy(String name, List<String> tags) {
        if (tags.isEmpty()) return query(getQueryOf(name));
        else return query(filteredByCall(name, tags));
    }

    @NonNull
    @Override
    protected Callable<CursorQuery> lastQuery() {
        if (lastTagsFilter.isEmpty()) {
            return super.lastQuery();
        } else {
            return filteredByCall(lastFilter, lastTagsFilter);
        }
    }


    @NonNull
    private Callable<CursorQuery> filteredByCall(@NonNull final String partOfName, @NonNull final Collection<String> tags) {
        return new Callable<CursorQuery>() {
            @Override
            public CursorQuery call() throws Exception {
                return filteredByQuery(partOfName, tags);
            }
        };
    }


    @NonNull
    public Observable<IngredientTemplate> update(@NonNull final IngredientTemplate ingredientTemplate,
                                                 @NonNull final Collection<Long> tagIds) {
        return fromDatabaseTask(updateCall(ingredientTemplate, tagIds));
    }

    @NonNull
    public Observable<IngredientTemplate> unParcel(@NonNull IngredientTypeParcel typeParcel) {
        return typeParcel.getWhenReady().get(session());
    }

    @Override
    public void performReadEntity(@NonNull Cursor cursor, @NonNull IngredientTemplate output) {
        dao().readEntity(cursor, output, 0);
    }

    @NonNull
    @Override
    public IngredientTemplate performGetById(long id) {
        IngredientTemplate template = dao().load(id);
        template.resetChildIngredients();
        template.resetTags();
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
    public Observable<IngredientTemplate> addNew(@NonNull final IngredientTemplate data,
                                                 @NonNull final Collection<Long> tagIds) {
        return fromDatabaseTask(insertNewCall(data, tagIds));
    }


    @NonNull
    private Callable<IngredientTemplate> updateCall(@NonNull final IngredientTemplate ingredientTemplate, @NonNull final Collection<Long> tagIds) {
        return new Callable<IngredientTemplate>() {
            @Override
            public IngredientTemplate call() throws Exception {
                List<JointIngredientTag> jTags = ingredientTemplate.getTags();
                for (JointIngredientTag jTag : jTags) {
                    if (!tagIds.contains(jTag.getTagId())) {
                        jTag.delete();
                    }
                }
                Collection<Long> currentTagIds = Collections2.transform(jTags, intoTagIds());
                for (Long tagId : tagIds) {
                    if (!currentTagIds.contains(tagId)) {
                        addJointTagWithIngredientTemplate(ingredientTemplate, tagId);
                    }
                }
                dao().update(ingredientTemplate);
                ingredientTemplate.resetTags();
                ingredientTemplate.getTags();
                return ingredientTemplate;
            }
        };
    }

    @NonNull
    private Callable<IngredientTemplate> insertNewCall(@NonNull final IngredientTemplate data,
                                                       @NonNull final Collection<Long> tagIds) {
        return new Callable<IngredientTemplate>() {
            @Override
            public IngredientTemplate call() throws Exception {
                IngredientTemplate template = performInsert(data);
                for (Long tagId : tagIds) {
                    addJointTagWithIngredientTemplate(template, tagId);
                }
                template.refresh();
                template.resetTags();
                template.getTags();
                return template;
            }
        };
    }

    private void addJointTagWithIngredientTemplate(@NonNull IngredientTemplate template,
                                                   @NonNull Long tagId) {
        JointIngredientTagDao jointDao = session().getJointIngredientTagDao();
        TagDao tagDao = session().getTagDao();
        Tag tag = tagDao.load(tagId);
        JointIngredientTag join = new JointIngredientTag(null);
        join.setTag(tag);
        join.setIngredientType(template);
        jointDao.insert(join);
        tag.resetIngredientTypes();
        tag.getIngredientTypes();
    }

    @Override
    protected void performRemove(@NonNull IngredientTemplate data) {
        performRemoveRaw(data);
    }

    @NonNull
    public RemovalEffect performRemoveRaw(@NonNull IngredientTemplate data) {
        List<Ingredient> childIngredients = data.getChildIngredients();
        List<Meal> removedMeals = new LinkedList<>();
        List<JointIngredientTag> tags = data.getTags();
        data.delete();
        for (Ingredient ingredient : childIngredients) {
            Meal meal = ingredient.getPartOfMeal();
            meal.resetIngredients();
            List<Ingredient> ingredients = meal.getIngredients();
            ingredient.delete();
            ingredients.remove(ingredient);
            if (ingredients.isEmpty()) {
                meal.delete();
                removedMeals.add(meal);
            }
        }
        for (JointIngredientTag join : tags) {
            join.delete();
        }
        session().clear();
        return new RemovalEffect(data, childIngredients, tags, removedMeals);
    }

    @Override
    protected void performRemoveAll() {
        dao().deleteAll();
    }

    @NonNull
    @Override
    protected CursorQuery allSortedByName() {
        return dao().queryBuilder()
                .orderAsc(Properties.Name)
                .buildCursor();
    }

    @NonNull
    @Override
    protected CursorQuery filteredSortedByNameQuery() {
        return dao().queryBuilder()
                .where(Properties.Name.like(""))
                .orderAsc(Properties.Name)
                .buildCursor();
    }

    @NonNull
    @Override
    protected Property getKeyProperty() {
        return Properties.Id;
    }

    @Override
    protected long getKey(@NonNull IngredientTemplate ingredientTemplate) {
        return ingredientTemplate.getId();
    }

    @NonNull
    private static Function<JointIngredientTag, Long> intoTagIds() {
        return new Function<JointIngredientTag, Long>() {
            @Nullable
            @Override
            public Long apply(JointIngredientTag jTag) {
                return jTag.getTagId();
            }
        };
    }


    @NonNull
    private CursorQuery filteredByQuery(@NonNull final String partOfName,
                                        @NonNull final Collection<String> tags) {
        cacheLastQuery(partOfName, tags);

        QueryBuilder<IngredientTemplate> builder = dao().queryBuilder();
        if (!partOfName.isEmpty()) builder.where(IngredientTemplateDao.Properties.Name.like("%" + partOfName + "%"));
        if (!tags.isEmpty()) {
            Join jTags = builder.join(JointIngredientTag.class, JointIngredientTagDao.Properties.IngredientTypeId);
            Join tagsJoin = builder.join(jTags, JointIngredientTagDao.Properties.TagId, Tag.class, TagDao.Properties.Id);
            tagsJoin.where(TagDao.Properties.Name.in(tags));
            builder.distinct();
        }
        return builder
                .orderAsc(IngredientTemplateDao.Properties.Name)
                .buildCursor();
    }

    @Override
    protected void cacheLastQuery(@NonNull String partOfName) {
        super.cacheLastQuery(partOfName);
        lastTagsFilter = Collections.emptyList();
    }

    protected void cacheLastQuery(@NonNull final String partOfName,
                                  @NonNull final Collection<String> tags) {
        cacheLastQuery(partOfName);
        lastTagsFilter = tags.isEmpty() ? Collections.<String>emptyList()
                : new ArrayList<>(tags);
    }

}
