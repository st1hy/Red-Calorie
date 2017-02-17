package com.github.st1hy.countthemcalories.activities.overview.addweight;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.rx.AbstractRxDatabaseModel;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Weight;
import com.github.st1hy.countthemcalories.database.WeightDao;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import org.joda.time.DateTime;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;

@PerActivity
public class RxDbWeightModel extends AbstractRxDatabaseModel {

    @Inject
    public RxDbWeightModel(@NonNull Lazy<DaoSession> session) {
        super(session);
    }

    @NonNull
    @CheckResult
    public Observable<Void> insertOrUpdate(@NonNull Weight weight) {
        return fromDatabaseTask(() -> dao().insertOrReplace(weight))
                .map(Functions.INTO_VOID);
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
