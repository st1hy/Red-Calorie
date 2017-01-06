package com.github.st1hy.countthemcalories.activities.tags.fragment.model;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.github.st1hy.countthemcalories.core.rx.RxDatabaseModel;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;

import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.query.CursorQuery;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

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
        else return query(filteredExcludeQueryCall(partOfName, excludedTags));
    }

    @NonNull
    @Override
    protected Callable<CursorQuery> lastQuery() {
        if (lastExcluded.isEmpty()) {
            return super.lastQuery();
        } else {
            return filteredExcludeQueryCall(lastFilter, lastExcluded);
        }
    }

    @NonNull
    private Callable<CursorQuery> filteredExcludeQueryCall(@NonNull final String partOfName, @NonNull final Collection<String> excludedTags) {
        return () -> filteredExcludeSortedQuery(partOfName, excludedTags);
    }

    @NonNull
    private CursorQuery filteredExcludeSortedQuery(@NonNull final String partOfName,
                                                   @NonNull final Collection<String> excludedTags) {
        cacheLastQuery(partOfName, excludedTags);
        QueryBuilder<Tag> builder = dao().queryBuilder();
        final WhereCondition notInList = TagDao.Properties.Name.notIn(excludedTags);
        if (!partOfName.isEmpty()) {
            builder = builder.where(TagDao.Properties.Name.like("%" + partOfName + "%"), notInList);
        } else {
            builder = builder.where(notInList);
        }
        return builder
                .orderAsc(TagDao.Properties.Name)
                .buildCursor();
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
