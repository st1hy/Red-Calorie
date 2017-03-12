package com.github.st1hy.countthemcalories.activities.tags.fragment.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.I18n;
import com.github.st1hy.countthemcalories.database.I18nDao;

import org.greenrobot.greendao.query.Query;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.Lazy;
import dagger.internal.SingleCheck;

@Singleton
public class I18nModel {

    @Inject
    Lazy<DaoSession> session;

    private Provider<Query<I18n>> queryProvider = SingleCheck.provider(() -> session().getI18nDao()
            .queryBuilder()
            .where(I18nDao.Properties.English.eq(null))
            .build());

    @Inject
    public I18nModel() {
    }

    @NonNull
    protected DaoSession session() {
        return session.get();
    }

    @Nullable
    public I18n findByName(String name) {
        Query<I18n> i18nQuery = queryProvider.get()
                .forCurrentThread();
        i18nQuery.setParameter(0, name);
        return i18nQuery.unique();
    }
}
