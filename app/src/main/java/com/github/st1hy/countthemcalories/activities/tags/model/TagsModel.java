package com.github.st1hy.countthemcalories.activities.tags.model;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.rx.ObservableValue;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class TagsModel {
    final DaoSession session;
    private List<Tag> tags = Collections.emptyList();
    private final ObservableValue<DbProcessing> dbProcessingValue = new ObservableValue<>(DbProcessing.NOT_STARTED);
    private Query<Tag> allSortedByNameQuery;

    @Inject
    public TagsModel(@NonNull DaoSession session) {
        this.session = session;
    }

    public Observable<List<Tag>> getTags() {
        return Observable.fromCallable(callInTx(loadTags()))
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

    public Observable<Void> addTag(@NonNull String tagName) {
        return Observable.fromCallable(callInTx(addTagCall(tagName)))
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(onStarted())
                .doOnTerminate(onFinished());
    }

    public Observable<List<Tag>> addTagAndRefresh(@NonNull String tagName) {
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
                return loadTags().call();
            }
        };
    }

    @NonNull
    private Callable<List<Tag>> addTagAndRefreshCall(@NonNull final String tagName) {
        return new Callable<List<Tag>>() {
            @Override
            public List<Tag> call() throws Exception {
                addTagCall(tagName).call();
                return loadTags().call();
            }
        };
    }

    @NonNull
    private Callable<Void> addTagCall(@NonNull final String tagName) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Tag tag = new Tag();
                tag.setName(tagName);
                session.getTagDao().insert(tag);
                return null;
            }
        };
    }

    @NonNull
    private Callable<List<Tag>> loadTags() {
        return new Callable<List<Tag>>() {
            @Override
            public List<Tag> call() throws Exception {
                List<Tag> tags = allSortedByName().list();
                for (Tag tag : tags) {
                    tag.getIngredientTypes();
                }
                TagsModel.this.tags = tags;
                return TagsModel.this.tags;
            }
        };
    }

    Query<Tag> allSortedByName() {
        if (allSortedByNameQuery == null) {
            QueryBuilder<Tag> queryBuilder = session.getTagDao().queryBuilder();
            queryBuilder.orderAsc(TagDao.Properties.Name);
            allSortedByNameQuery = queryBuilder.build();
        }
        return allSortedByNameQuery;
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

    public enum DbProcessing {
        NOT_STARTED, STARTED, FINISHED
    }
}
