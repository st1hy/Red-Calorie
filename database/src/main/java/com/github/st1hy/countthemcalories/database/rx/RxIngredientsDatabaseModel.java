package com.github.st1hy.countthemcalories.database.rx;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.I18n;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTagJoint;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao.Properties;
import com.github.st1hy.countthemcalories.database.JointIngredientTagDao;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;
import com.github.st1hy.countthemcalories.database.property.CreationSource;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.CursorQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import dagger.internal.DoubleCheck;
import rx.Observable;

@Singleton
public class RxIngredientsDatabaseModel extends RxDatabaseModel<IngredientTemplate> {

    private static final int I18N_OFFSET = 6;
    private final Lazy<IngredientTemplateDao> dao;
    private Collection<String> lastTagsFilter = Collections.emptyList();
    @Inject
    I18nModel i18nModel;

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
        session().getI18nDao().readEntity(cursor, output.getTranslations(), I18N_OFFSET);
    }

    @NonNull
    @Override
    public IngredientTemplate performGetById(long id) {
        IngredientTemplate template = dao().load(id);
        template.resetChildIngredients();
        template.resetTags();
        template.getTags();
        template.getChildIngredients();
        loadTranslation(template);
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
            ingredientTemplate.resetTags();
            List<IngredientTagJoint> jTags = ingredientTemplate.getTags();
            for (IngredientTagJoint jTag : jTags) {
                if (!tagIds.contains(jTag.getTagId())) {
                    jTag.delete();
                }
            }
            Collection<Long> currentTagIds = Collections2.transform(jTags, IngredientTagJoint::getTagId);
            for (Long tagId : tagIds) {
                if (!currentTagIds.contains(tagId)) {
                    addJointTagWithIngredientTemplate(ingredientTemplate, tagId);
                }
            }
            dao().update(ingredientTemplate);
            ingredientTemplate.resetTags();
            ingredientTemplate.getTags();
            session().clear(); //reset ingredients cache so it updates them correctly
            return ingredientTemplate;
        };
    }

    private void addJointTagWithIngredientTemplate(@NonNull IngredientTemplate template,
                                                   @NonNull Long tagId) {
        JointIngredientTagDao jointDao = session().getJointIngredientTagDao();
        TagDao tagDao = session().getTagDao();
        Tag tag = tagDao.load(tagId);
        IngredientTagJoint join = new IngredientTagJoint(null);
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
    public IngredientRemovalEffect performRemoveRaw(@NonNull IngredientTemplate data) {
        List<Ingredient> childIngredients = data.getChildIngredients();
        List<Meal> removedMeals = new LinkedList<>();
        List<IngredientTagJoint> tags = data.getTags();
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
        for (IngredientTagJoint join : tags) {
            join.delete();
        }
        session().clear();
        return new IngredientRemovalEffect(data, childIngredients, tags, removedMeals);
    }

    @Override
    protected void performRemoveAll() {
        dao().deleteAll();
    }

    @NonNull
    protected Callable<CursorQuery> getQueryOf(@NonNull final String partOfName) {
        return () -> {
            cacheLastQuery(partOfName);
            if (Strings.isNullOrEmpty(partOfName)) {
                return allSortedByNameSingleton().forCurrentThread();
            } else {
                CursorQuery query = filteredSortedByNameQuery().forCurrentThread();
                String search = "%" + partOfName + "%";
                query.setParameter(0, search);
                query.setParameter(1, search);
                return query;
            }
        };
    }

    @NonNull
    @Override
    protected CursorQuery allSortedByName() {
        return CursorQuery.internalCreate(dao(), "SELECT I.*, N.* FROM INGREDIENTS_TEMPLATE I LEFT JOIN i18n N ON I.name = N.en ORDER BY I.name ASC;", new Object[0]);
    }

    @NonNull
    @Override
    protected CursorQuery filteredSortedByNameQuery() {
        String sql = "SELECT I.*, N.* FROM INGREDIENTS_TEMPLATE I LEFT JOIN i18n N ON I.name = N.en" +
                " WHERE (" +
                " I.creation_source = 1 AND I.name LIKE ?" +
                ") OR (" +
                " I.creation_source = 0 AND N." + I18n.selectColumnByLocale(Locale.getDefault()) + " LIKE ?" +
                " )" +
                " ORDER BY I.name ASC;";
        return CursorQuery.internalCreate(dao(), sql, new Object[]{"", ""});
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
    private CursorQuery filteredByQuery(@NonNull final String partOfName,
                                        @NonNull final Collection<String> tags) {
        cacheLastQuery(partOfName, tags);
        String tagArgs = RxTagsDatabaseModel.commaSeparatedArgList(tags);
        String i18nColumn = I18n.selectColumnByLocale(Locale.getDefault());
        boolean hasName = !partOfName.isEmpty();
        boolean hasTags = !tags.isEmpty();
        List<String> arguments = new ArrayList<>(2 + tags.size() * 2);
        String search = "%" + partOfName + "%";

        StringBuilder sql = new StringBuilder(600);
        sql.append("SELECT DISTINCT I.*, N.* ");
        sql.append("FROM INGREDIENTS_TEMPLATE I ");
        sql.append("LEFT JOIN i18n N ON I.name = N.en ");
        if (hasTags) {
            sql.append("JOIN INGREDIENT_TAG_JOINTS J ON I._id = J.INGREDIENT_TYPE_ID ");
            sql.append("JOIN ( SELECT T._id FROM TAGS T LEFT JOIN i18n N ON T.NAME = N.en ");
            sql.append("WHERE ( T.creation_source = 1 ");
            sql.append("AND T.NAME IN ( ").append(tagArgs).append(" ) ");
            arguments.addAll(tags);
            sql.append(") OR (").append(" T.creation_source = 0 ");
            sql.append("AND N.").append(i18nColumn).append(" IN ( ").append(tagArgs).append(" ) ");
            arguments.addAll(tags);
            sql.append(")) T ON T._id = J.TAG_ID ");
        }
        if (hasName) {
            sql.append(" WHERE ( ");
            sql.append(" I.creation_source = 1");
            sql.append(" AND I.name LIKE ?");
            arguments.add(search);
            sql.append(") OR (");
            sql.append(" I.creation_source = 0");
            sql.append(" AND N.").append(i18nColumn).append(" LIKE ?");
            arguments.add(search);
            sql.append(")");
        }
        if (hasTags) {
            sql.append(" GROUP BY I._id HAVING COUNT(I._id) = ").append(tags.size());
        }
        sql.append(" ORDER BY I.name ASC;");
        return CursorQuery.internalCreate(dao(), sql.toString(), arguments.toArray());
    }

    @Override
    protected void cacheLastQuery(@NonNull String partOfName) {
        super.cacheLastQuery(partOfName);
        lastTagsFilter = Collections.emptyList();
    }

    protected void cacheLastQuery(@NonNull final String partOfName,
                                  @NonNull final Collection<String> tags) {
        cacheLastQuery(partOfName);
        lastTagsFilter = tags.isEmpty() ? Collections.emptyList() : new ArrayList<>(tags);
    }

    /**
     * Loads ingredient template by id and then loads all tags in this ingredient template.
     */
    @NonNull
    public Observable<IngredientTemplate> getByIdRecursive(final long id) {
        return fromDatabaseTask(() -> {
            IngredientTemplate ingredientTemplate = performGetById(id);
            for (IngredientTagJoint ingredientTagJoint : ingredientTemplate.getTags()) {
                Tag tag = ingredientTagJoint.getTag();
                loadTranslation(tag);
            }
            return ingredientTemplate;
        });
    }

    private void loadTranslation(@NonNull IngredientTemplate ingredient) {
        String name = ingredient.getName();
        if (ingredient.getCreationSource() == CreationSource.GENERATED) {
            ingredient.setTranslations(i18nModel.findByName(name));
        }
    }

    private void loadTranslation(@NonNull Tag tag) {
        String name = tag.getName();
        if (tag.getCreationSource() == CreationSource.GENERATED) {
            tag.setTranslations(i18nModel.findByName(name));
        }
    }
}
