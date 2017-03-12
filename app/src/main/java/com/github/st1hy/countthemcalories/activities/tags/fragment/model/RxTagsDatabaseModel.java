package com.github.st1hy.countthemcalories.activities.tags.fragment.model;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.github.st1hy.countthemcalories.core.rx.RxDatabaseModel;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.I18n;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;
import com.github.st1hy.countthemcalories.database.property.CreationSource;
import com.google.common.base.Strings;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.CursorQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import dagger.internal.DoubleCheck;
import rx.Observable;


@Singleton
public class RxTagsDatabaseModel extends RxDatabaseModel<Tag> {
    private final Lazy<TagDao> dao;
    private List<String> lastExcluded = Collections.emptyList();
    private static final int COLUMN_COUNT_NUMBER = 3;
    private static final int I18N_OFFSET = 4;
    @Inject
    I18nModel i18nModel;

    @Inject
    public RxTagsDatabaseModel(@NonNull Lazy<DaoSession> lazySession) {
        super(lazySession);
        this.dao = DoubleCheck.lazy(() -> session().getTagDao());
    }

    @NonNull
    public Observable<Cursor> updateRefresh(@NonNull Tag tag) {
        return fromDatabaseTask(() -> {
            dao().update(tag);
            session().clear(); //clears ingredients cache
            return refresh().call();
        });
    }

    @Override
    public void performReadEntity(@NonNull Cursor cursor, @NonNull Tag output) {
        dao().readEntity(cursor, output, 0);
        int counter = cursor.getInt(COLUMN_COUNT_NUMBER);
        output.setIngredientCount(counter);
        session().getI18nDao().readEntity(cursor, output.getTranslations(), I18N_OFFSET);
    }

    @NonNull
    public Observable<Cursor> getAllFiltered(@NonNull String partOfName,
                                             @NonNull Collection<String> excludedTags) {
        return fromDatabaseTask(filteredExclude(partOfName, excludedTags));
    }

    @NonNull
    @Override
    public Tag performGetById(long id) {
        Tag tag = dao().load(id);
        tag.resetIngredientTypes();
        tag.getIngredientTypes();
        loadTranslation(tag);
        return tag;
    }

    @NonNull
    @Override
    public Tag performInsert(@NonNull Tag data) {
        dao().insert(data);
        return data;
    }

    @Override
    protected void performRemove(@NonNull Tag tag) {
        rawRemove(tag);
    }

    @NonNull
    public Pair<Tag, List<JointIngredientTag>> rawRemove(@NonNull Tag tag) {
        List<JointIngredientTag> joinTagIngredients = tag.getIngredientTypes();
        tag.delete();
        for (JointIngredientTag join : joinTagIngredients) {
            IngredientTemplate ingredient = join.getIngredientType();
            join.delete();
            ingredient.getTags().remove(join);
        }
        return Pair.create(tag, joinTagIngredients);
    }

    @Override
    protected void performRemoveAll() {
        dao().deleteAll();
    }

    @NonNull
    @Override
    protected CursorQuery allSortedByName() {
        return CursorQuery.internalCreate(
                dao(), "SELECT T.*, ( SELECT COUNT(I._id) FROM INGREDIENT_TAG_JOINTS I WHERE T._id = I.tag_id ), N.* FROM TAGS T LEFT JOIN i18n N ON T.NAME = N.en ORDER BY T.NAME ASC;"
                , new Object[]{});
    }

    @NonNull
    @Override
    protected CursorQuery filteredSortedByNameQuery() {
        return CursorQuery.internalCreate(
                dao(), "SELECT T.*, ( SELECT COUNT(I._id) FROM INGREDIENT_TAG_JOINTS I WHERE T._id = I.tag_id ), N.* FROM TAGS T LEFT JOIN i18n N ON T.NAME = N.en WHERE (T.creation_source = 1 AND T.name LIKE ?) OR (T.creation_source = 0 AND " +
                        "N." + I18n.selectColumnByLocale(Locale.getDefault()) + " LIKE ?) ORDER BY T.name ASC;"
                , new Object[]{"", ""});
    }

    @NonNull
    @Override
    protected Property getKeyProperty() {
        return TagDao.Properties.Id;
    }

    @Override
    protected long getKey(@NonNull Tag tag) {
        return tag.getId();
    }

    private TagDao dao() {
        return dao.get();
    }

    @NonNull
    private Callable<Cursor> filteredExclude(@NonNull final String partOfName,
                                             @NonNull final Collection<String> excludedTags) {
        if (excludedTags.isEmpty()) return query(getQueryOf(partOfName));
        else return query(() -> filteredExcludeSortedQuery(partOfName, excludedTags));
    }

    @NonNull
    @Override
    protected Callable<CursorQuery> lastQuery() {
        if (lastExcluded.isEmpty()) {
            return super.lastQuery();
        } else {
            return () -> filteredExcludeSortedQuery(lastFilter, lastExcluded);
        }
    }

    @Override
    @NonNull
    protected Callable<CursorQuery> getQueryOf(@NonNull final String partOfName) {
        return () -> {
            cacheLastQuery(partOfName);
            if (Strings.isNullOrEmpty(partOfName)) {
                return allSortedByNameSingleton().forCurrentThread();
            } else {
                CursorQuery query = filteredSortedByNameQuery();
                String search = "%" + partOfName + "%";
                query.setParameter(0, search);
                query.setParameter(1, search);
                return query;
            }
        };
    }

    @NonNull
    private CursorQuery filteredExcludeSortedQuery(@NonNull final String partOfName,
                                                   @NonNull final Collection<String> excludedTags) {
        cacheLastQuery(partOfName, excludedTags);

        String i18nColumn = "N." + I18n.selectColumnByLocale(Locale.getDefault());
        String excluded = excludedTags(excludedTags);

        StringBuilder sql = new StringBuilder(400);
        sql.append("SELECT T.*, ( SELECT COUNT(I._id) FROM INGREDIENT_TAG_JOINTS I WHERE T._id = I.tag_id ), N.* FROM TAGS T LEFT JOIN i18n N ON T.NAME = N.en");
        sql.append(" WHERE (").append(" T.creation_source = 1 AND T.name LIKE ?");
        if (!excludedTags.isEmpty()) {
            sql.append(" AND T.name NOT IN (").append(excluded).append(")");
        }
        sql.append(")");
        sql.append(" OR (");
        sql.append(" T.creation_source = 0 AND ").append(i18nColumn).append(" LIKE ?");
        if (!excludedTags.isEmpty()) {
            sql.append(" AND ").append(i18nColumn).append(" NOT IN (").append(excluded).append(")");

        }
        sql.append(")");
        sql.append(" ORDER BY T.name ASC;");
        List<Object> list = new ArrayList<>(2 + (excludedTags.size() << 1));
        String search = "%" + partOfName + "%";
        list.add(search);
        list.add(search);
        list.addAll(excludedTags);
        list.addAll(excludedTags);
        return CursorQuery.internalCreate(dao(), sql.toString(), list.toArray());
    }

    @NonNull
    private String excludedTags(@NonNull Collection<String> excludedTags) {
        if (!excludedTags.isEmpty()) {
            StringBuilder tagsListBuilder = new StringBuilder(50);
            for (int i = 0, n = excludedTags.size(); i < n; i++) {
                tagsListBuilder.append("?");
                if (i < n - 1) tagsListBuilder.append(",");
            }
            return tagsListBuilder.toString();
        }
        return "";
    }

    @Override
    protected void cacheLastQuery(@NonNull String partOfName) {
        super.cacheLastQuery(partOfName);
        lastExcluded = Collections.emptyList();
    }

    protected void cacheLastQuery(@NonNull final String partOfName,
                                  @NonNull final Collection<String> excludedTags) {
        cacheLastQuery(partOfName);
        lastExcluded = excludedTags.isEmpty() ? Collections.emptyList()
                : new ArrayList<>(excludedTags);
    }

    private void loadTranslation(@NonNull Tag tag) {
        String name = tag.getName();
        if (tag.getCreationSource() == CreationSource.GENERATED) {
            tag.setTranslations(i18nModel.findByName(name));
        }
    }
}
