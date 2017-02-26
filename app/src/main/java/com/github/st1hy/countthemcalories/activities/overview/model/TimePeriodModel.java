package com.github.st1hy.countthemcalories.activities.overview.model;

import android.database.Cursor;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.rx.AbstractRxDatabaseModel;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientDao;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao;
import com.github.st1hy.countthemcalories.database.MealDao;
import com.github.st1hy.countthemcalories.database.Weight;
import com.github.st1hy.countthemcalories.database.WeightDao;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import org.greenrobot.greendao.query.CursorQuery;
import org.greenrobot.greendao.query.Query;
import org.joda.time.DateTime;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Lazy;
import dagger.internal.SingleCheck;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

@PerActivity
public class TimePeriodModel extends AbstractRxDatabaseModel {

    private final Provider<CursorQuery> queryProvider = SingleCheck.provider(() -> {
        StringBuilder sql = new StringBuilder(512);
        sql.append("SELECT ");
        sql.append("M.").append(MealDao.Properties.CreationDate.columnName).append(", ");
        sql.append("I.").append(IngredientDao.Properties.Amount.columnName).append(", ");
        sql.append("IT.").append(IngredientTemplateDao.Properties.EnergyDensityAmount.columnName).append(", ");
        sql.append("IT.").append(IngredientTemplateDao.Properties.AmountType.columnName).append(" ");
        sql.append("from ").append(MealDao.TABLENAME).append(" M ");
        sql.append("join ").append(IngredientDao.TABLENAME).append(" I ");
        sql.append("on I.").append(IngredientDao.Properties.PartOfMealId.columnName)
                .append(" = M.").append(MealDao.Properties.Id.columnName).append(" ");
        sql.append("join ").append(IngredientTemplateDao.TABLENAME).append(" IT ");
        sql.append("on I.").append(IngredientDao.Properties.IngredientTypeId.columnName)
                .append(" = IT.").append(IngredientTemplateDao.Properties.Id.columnName).append(" ");
        sql.append("where M.").append(MealDao.Properties.CreationDate.columnName).append(" ");
        sql.append("between ").append("(?) and (?) ");
        sql.append("order by M.").append(MealDao.Properties.CreationDate.columnName).append(" asc;");
        return CursorQuery.internalCreate(dao(), sql.toString(), new Object[2]);
    });

    private final Provider<Query<Weight>> weightQueryProvider = SingleCheck.provider(() -> session()
            .getWeightDao()
            .queryBuilder()
            .where(WeightDao.Properties.MeasurementDate.between(null, null))
            .orderAsc(WeightDao.Properties.MeasurementDate)
            .build());

    private DateTime start, end;
    private final PublishSubject<TimePeriod> updates = PublishSubject.create();

    @Inject
    public TimePeriodModel(@NonNull Lazy<DaoSession> session) {
        super(session);
    }

    @NonNull
    @CheckResult
    public Observable<TimePeriod> updates() {
        return updates.asObservable();
    }

    public void refresh() {
        if (start != null && end != null) {
            refresh(start, end);
        }
    }

    public void refresh(@NonNull final DateTime start, @NonNull final DateTime end) {
        this.start = start;
        this.end = end;
        loadData(start, end)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updates::onNext);
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources") //Min Api 19, current min 16
    @NonNull
    @CheckResult
    private Observable<TimePeriod> loadData(@NonNull final DateTime start,
                                            @NonNull final DateTime end) {
        return fromDatabaseTask(() -> {
            CursorQuery query = queryProvider.get().forCurrentThread();
            query.setParameter(0, start.getMillis());
            query.setParameter(1, end.getMillis());
            Cursor cursor = query.query();
            Query<Weight> weightQuery = weightQueryProvider.get().forCurrentThread();
            weightQuery.setParameter(0, start.getMillis() - 1);
            weightQuery.setParameter(1, end.getMillis() + 1);
            List<Weight> list = weightQuery.listLazy();
            try {
                return new TimePeriod.Builder(cursor, start, end, list).build();
            } finally {
                cursor.close();
            }
        });
    }

    @NonNull
    private MealDao dao() {
        return session().getMealDao();
    }
}
