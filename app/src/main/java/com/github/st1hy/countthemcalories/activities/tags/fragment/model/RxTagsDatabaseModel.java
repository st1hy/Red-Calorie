package com.github.st1hy.countthemcalories.activities.tags.fragment.model;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.github.st1hy.countthemcalories.core.rx.RxDatabaseModel;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.JointIngredientTagDao;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.CursorQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
    private static final String COLUMN_COUNT = "cntr";

    @Inject
    public RxTagsDatabaseModel(@NonNull Lazy<DaoSession> lazySession) {
        super(lazySession);
        this.dao = DoubleCheck.lazy(() -> session().getTagDao());
    }

    @NonNull
    public Observable<Cursor> updateRefresh(@NonNull Tag tag) {
        return fromDatabaseTask(() -> {
            dao().update(tag);
            return refresh().call();
        });
    }

    @Override
    public void performReadEntity(@NonNull Cursor cursor, @NonNull Tag output) {
        dao().readEntity(cursor, output, 0);
        int counter = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT));
        output.setIngredientCount(counter);
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
                dao(), "SELECT " +
                        "T." + TagDao.Properties.Id.columnName + ", " +
                        "T." + TagDao.Properties.Name.columnName + ", " +
                        "(" +
                        "SELECT " +
                        "count(*) " +
                        " FROM " +
                        JointIngredientTagDao.TABLENAME + " I " +
                        " WHERE " +
                        " T." + TagDao.Properties.Id.columnName +
                        " = " +
                        " I." + JointIngredientTagDao.Properties.TagId.columnName +
                        ") " + COLUMN_COUNT +
                        " FROM " +
                        TagDao.TABLENAME + " T " +
                        "ORDER BY " + TagDao.Properties.Name.columnName + " ASC;"
                , new Object[]{});
    }

    @NonNull
    @Override
    protected CursorQuery filteredSortedByNameQuery() {
        return CursorQuery.internalCreate(
                dao(), "SELECT " +
                        "T." + TagDao.Properties.Id.columnName + ", " +
                        "T." + TagDao.Properties.Name.columnName + ", " +
                        "(" +
                        "SELECT " +
                        "count(*) " +
                        " FROM " +
                        JointIngredientTagDao.TABLENAME + " I " +
                        " WHERE " +
                        " T." + TagDao.Properties.Id.columnName +
                        " = " +
                        " I." + JointIngredientTagDao.Properties.TagId.columnName +
                        ") " + COLUMN_COUNT +
                        " FROM " +
                        TagDao.TABLENAME + " T " +
                        "WHERE " +
                        "T." + TagDao.Properties.Name.columnName + " LIKE ? " +
                        "ORDER BY " + TagDao.Properties.Name.columnName + " ASC;"
                , new Object[]{""});
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

    @NonNull
    private CursorQuery filteredExcludeSortedQuery(@NonNull final String partOfName,
                                                   @NonNull final Collection<String> excludedTags) {
        cacheLastQuery(partOfName, excludedTags);

        StringBuilder sql = new StringBuilder(400);
        sql.append("SELECT ");
        sql.append("T.").append(TagDao.Properties.Id.columnName).append(", ");
        sql.append("T.").append(TagDao.Properties.Name.columnName).append(", ");
        sql.append("(")
                .append("SELECT ")
                .append("count(*) ")
                .append(" FROM ")
                .append(JointIngredientTagDao.TABLENAME).append(" I ")
                .append(" WHERE ")
                .append(" T.").append(TagDao.Properties.Id.columnName)
                .append(" = ")
                .append(" I.").append(JointIngredientTagDao.Properties.TagId.columnName)
                .append(") ").append(COLUMN_COUNT);
        sql.append(" FROM ");
        sql.append(TagDao.TABLENAME).append(" T ");
        sql.append("WHERE ");
        sql.append("T.").append(TagDao.Properties.Name.columnName).append(" LIKE ? ");
        if (!excludedTags.isEmpty()) {
            sql.append(" AND ");
            sql.append("T.").append(TagDao.Properties.Name.columnName);
            sql.append(" NOT IN ").append("(");
            for (int i = 0, n = excludedTags.size(); i < n; i++) {
                sql.append("?");
                if (i < n - 1) sql.append(",");
            }
            sql.append(")");

        }
        sql.append(" ORDER BY ");
        sql.append(TagDao.Properties.Name.columnName).append(" ASC;");
        List<Object> list = new ArrayList<>(1 + excludedTags.size());
        list.add("%" + partOfName + "%");
        list.addAll(excludedTags);
        return CursorQuery.internalCreate(dao(), sql.toString(), list.toArray());
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


}
