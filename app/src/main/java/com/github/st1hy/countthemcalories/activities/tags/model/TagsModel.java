package com.github.st1hy.countthemcalories.activities.tags.model;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.rx.ObservableValue;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;
import com.google.common.base.Strings;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import de.greenrobot.dao.query.Query;
import rx.Observable;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class TagsModel {
    final DaoSession session;
    private List<Tag> tags = Collections.emptyList();
    private final ObservableValue<DbProcessing> dbProcessingValue = new ObservableValue<>(DbProcessing.NOT_STARTED);
    private Query<Tag> allSortedByNameQuery;
    private Query<Tag> filteredSortedByNameQuery;
    private String partOfLastQuery = "";

    @Inject
    public TagsModel(@NonNull DaoSession session) {
        this.session = session;
    }

    public Observable<List<Tag>> getTags() {
        return Observable.fromCallable(callInTx(loadAllTags()))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(onStarted())
                .doOnTerminate(onFinished());
    }

    public Observable<DbProcessing> getDbProcessingObservable() {
        return dbProcessingValue.asObservable();
    }

    @NonNull
    public Tag getItemAt(int position) {
        return tags.get(position);
    }

    public int getItemCount() {
        return tags.size();
    }


    @NonNull
    private Action0 onFinished() {
        return new Action0() {
            @Override
            public void call() {
                dbProcessingValue.setValue(DbProcessing.FINISHED);
            }
        };
    }

    @NonNull
    private Action0 onStarted() {
        return new Action0() {
            @Override
            public void call() {
                dbProcessingValue.setValue(DbProcessing.STARTED);
            }
        };
    }

    @StringRes
    public int getNewTagDialogTitle() {
        return R.string.tags_new_tag_dialog;
    }

    public Observable<Tag> addTag(@NonNull String tagName) {
        return Observable.fromCallable(callInTx(addTagCall(tagName)))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(onStarted())
                .doOnTerminate(onFinished());
    }

    /**
     * @return observable position of the added tag on the list
     */
    public Observable<Integer> addTagAndRefresh(@NonNull String tagName) {
        return Observable.fromCallable(callInTx(addTagAndRefreshCall(tagName)))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(onStarted())
                .doOnTerminate(onFinished());
    }

    public Observable<List<Tag>> removeTagAndRefresh(@NonNull Tag tag) {
        return Observable.fromCallable(callInTx(removeTagAndRefreshCall(tag)))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(onStarted())
                .doOnTerminate(onFinished());
    }

    private Callable<List<Tag>> removeTagAndRefreshCall(final Tag tag) {
        return new Callable<List<Tag>>() {
            @Override
            public List<Tag> call() throws Exception {
                tag.delete();
                return loadAllTags().call();
            }
        };
    }

    @NonNull
    private Callable<Integer> addTagAndRefreshCall(@NonNull final String tagName) {
        return new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Tag tag = addTagCall(tagName).call();
                loadAllTags().call();
                for (int i = 0; i < tags.size(); i++) {
                    if (getItemAt(i).getId().equals(tag.getId())) return i;
                }
                throw new IllegalStateException("Could not found newly added tag on the list!");
            }
        };
    }

    @NonNull
    private Callable<Tag> addTagCall(@NonNull final String tagName) {
        return new Callable<Tag>() {
            @Override
            public Tag call() throws Exception {
                Tag tag = new Tag();
                tag.setName(tagName);
                session.getTagDao().insert(tag);
                return tag;
            }
        };
    }

    @NonNull
    private Callable<List<Tag>> loadAllTags() {
        return tagsFiltered("");
    }

    @NonNull
    private <T> Callable<T> callInTx(@NonNull final Callable<T> task) {
        return new Callable<T>() {
            @Override
            public T call() throws Exception {
                return session.callInTx(task);
            }
        };
    }

    @NonNull
    public Observable<List<Tag>> getTagsFiltered(@NonNull String partOfName) {
        if (partOfName.equals(partOfLastQuery)) {
            return Observable.just(tags);
        } else {
            partOfLastQuery = partOfName;
            return Observable.fromCallable(callInTx(tagsFiltered(partOfName)))
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(onStarted())
                    .doOnTerminate(onFinished());
        }
    }

    @NonNull
    private Callable<List<Tag>> tagsFiltered(@NonNull final String partOfName) {
        return new Callable<List<Tag>>() {
            @Override
            public List<Tag> call() throws Exception {
                List<Tag> tags = getQueryOf(partOfName).list();
                for (Tag tag : tags) {
                    tag.getIngredientTypes();
                }
                TagsModel.this.tags = tags;
                return tags;
            }
        };
    }

    @NonNull
    private Query<Tag> getQueryOf(@NonNull String partOfName) {
        if (Strings.isNullOrEmpty(partOfName)) {
            return allSortedByName().forCurrentThread();
        } else {
            Query<Tag> query = filteredSortedByNameQuery().forCurrentThread();
            query.setParameter(0, "%" + partOfName + "%");
            return query;
        }
    }

    Query<Tag> allSortedByName() {
        if (allSortedByNameQuery == null) {
            allSortedByNameQuery = session.getTagDao()
                    .queryBuilder()
                    .orderAsc(TagDao.Properties.Name)
                    .build();
        }
        return allSortedByNameQuery;
    }

    Query<Tag> filteredSortedByNameQuery() {
        if (filteredSortedByNameQuery == null) {
            filteredSortedByNameQuery = session.getTagDao()
                    .queryBuilder()
                    .where(TagDao.Properties.Name.like(""))
                    .orderAsc(TagDao.Properties.Name)
                    .build();
        }
        return filteredSortedByNameQuery;
    }

    public Observable<List<Tag>> clear() {
        return Observable.fromCallable(callInTx(removeAll()))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(onStarted())
                .doOnTerminate(onFinished());
    }

    @NonNull
    private Callable<List<Tag>> removeAll() {
        return new Callable<List<Tag>>() {
            @Override
            public List<Tag> call() throws Exception {
                session.getTagDao().deleteAll();
                return loadAllTags().call();
            }
        };
    }

    public enum DbProcessing {
        NOT_STARTED, STARTED, FINISHED
    }
}
