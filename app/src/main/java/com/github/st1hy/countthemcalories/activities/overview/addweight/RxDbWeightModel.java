package com.github.st1hy.countthemcalories.activities.overview.addweight;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.rx.AbstractRxDatabaseModel;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Weight;
import com.github.st1hy.countthemcalories.database.WeightDao;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.google.common.collect.Iterables;

import java.util.List;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;
import timber.log.Timber;

@PerActivity
public class RxDbWeightModel extends AbstractRxDatabaseModel {

    @Inject
    public RxDbWeightModel(@NonNull Lazy<DaoSession> session) {
        super(session);
    }

    public Observable<Void> insertOrUpdate(@NonNull Weight weight) {
        return fromDatabaseTask(() -> getaLong(weight))
                .map(Functions.INTO_VOID);
    }

    private long getaLong(@NonNull Weight weight) {
        long l = dao().insertOrReplace(weight);
        List<Weight> weights = dao().loadAll();
        Timber.d(Iterables.toString(weights));
        return l;
    }

    public WeightDao dao() {
        return session().getWeightDao();
    }
}
