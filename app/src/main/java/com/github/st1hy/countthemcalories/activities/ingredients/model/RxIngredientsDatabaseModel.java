package com.github.st1hy.countthemcalories.activities.ingredients.model;

import android.database.Cursor;
import android.support.annotation.NonNull;

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
import com.google.common.collect.Collections2;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.CursorQuery;
import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import dagger.internal.DoubleCheck;
import rx.Observable;

@Singleton
public class RxIngredientsDatabaseModel extends RxDatabaseModel<IngredientTemplate> {

    private final Lazy<IngredientTemplateDao> dao;
    private Collection<String> lastTagsFilter = Collections.emptyList();

    @Inject
    public RxIngredientsDatabaseModel(@NonNull Lazy<DaoSession> session) {
        super(session);
        this.dao = DoubleCheck.lazy(() -> session().getIngredientTemplateDao());
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
        return () -> filteredByQuery(partOfName, tags);
    }


    @NonNull
    public Observable<IngredientTemplate> insertOrUpdate(@NonNull final IngredientTemplate ingredientTemplate,
                                                         @NonNull final Collection<Long> tagIds) {
        return fromDatabaseTask(insertOrUpdateCall(ingredientTemplate, tagIds));
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
    private Callable<IngredientTemplate> insertOrUpdateCall(@NonNull final IngredientTemplate ingredientTemplate,
                                                            @NonNull final Collection<Long> tagIds) {
        return () -> {
            dao().insertOrReplace(ingredientTemplate);
            List<JointIngredientTag> jTags = ingredientTemplate.getTags();
            for (JointIngredientTag jTag : jTags) {
                if (!tagIds.contains(jTag.getTagId())) {
                    jTag.delete();
                }
            }
            Collection<Long> currentTagIds = Collections2.transform(jTags, JointIngredientTag::getTagId);
            for (Long tagId : tagIds) {
                if (!currentTagIds.contains(tagId)) {
                    addJointTagWithIngredientTemplate(ingredientTemplate, tagId);
                }
            }
            dao().update(ingredientTemplate);
            ingredientTemplate.resetTags();
            ingredientTemplate.getTags();
            return ingredientTemplate;
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

    @SuppressWarnings("unchecked")
    @NonNull
    private CursorQuery filteredByQuery(@NonNull final String partOfName,
                                        @NonNull final Collection<String> tags) {
        cacheLastQuery(partOfName, tags);
        QueryBuilder<IngredientTemplate> builder = dao().queryBuilder();

        if (!partOfName.isEmpty())
            builder.where(IngredientTemplateDao.Properties.Name.like("%" + partOfName + "%"));
        if (!tags.isEmpty()) {
            Join jTags = builder.join(JointIngredientTag.class, JointIngredientTagDao.Properties.IngredientTypeId);
            Join tagsJoin = builder.join(jTags, JointIngredientTagDao.Properties.TagId, Tag.class, TagDao.Properties.Id);
            tagsJoin.where(TagDao.Properties.Name.in(tags));
            // GreenDao don't support GROUP BY, so we hack.
            // We want only ingredients that have all the tags not just any of them, so we group them
            // and select only those having exactly as much rows as our tags, this works because
            // each row in group have unique tag from our list
            // Added here instead of in builder, because greenDao reorders WHERE clauses.
            // FIXME May break at update.
            tagsJoin.where(new WhereCondition.StringCondition("1 GROUP BY T.\"_id\" HAVING COUNT(T.\"_id\")=" + tags.size()));
            builder.distinct();
        }
        builder.orderAsc(IngredientTemplateDao.Properties.Name);
        return builder.buildCursor();
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

    /**
     * Loads ingredient template by id and then loads all tags in this ingredient template.
     */
    @NonNull
    public Observable<IngredientTemplate> getByIdRecursive(final long id) {
        return fromDatabaseTask(() -> {
            IngredientTemplate ingredientTemplate = performGetById(id);
            for (JointIngredientTag jointIngredientTag : ingredientTemplate.getTags()) {
                jointIngredientTag.getTag();
            }
            return ingredientTemplate;
        });
    }
}
