package com.github.st1hy.countthemcalories.database.rx;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Weight;
import com.github.st1hy.countthemcalories.database.WeightDao;

import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Lazy;
import rx.Observable;

@Singleton
public class RxDbWeightModel extends AbstractRxDatabaseModel {

    @Inject
    public RxDbWeightModel(@NonNull Lazy<DaoSession> session) {
        super(session);
    }

    @NonNull
    @CheckResult
    public Observable<Long> insertOrUpdate(@NonNull Weight weight) {
        return fromDatabaseTask(() -> dao().insertOrReplace(weight));
    }

    @NonNull
    @CheckResult
    public Observable<Weight> findOneByDate(@NonNull DateTime time) {
        return fromDatabaseTask(() ->
                dao().queryBuilder()
                        .where(WeightDao.Properties.MeasurementDate.eq(time.getMillis()))
                        .build()
                        .unique()
        );
    }

    private WeightDao dao() {
        return session().getWeightDao();
    }
}
