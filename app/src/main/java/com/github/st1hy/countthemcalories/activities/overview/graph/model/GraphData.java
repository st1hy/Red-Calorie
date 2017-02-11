package com.github.st1hy.countthemcalories.activities.overview.graph.model;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addmeal.model.PhysicalQuantitiesModel;
import com.github.st1hy.countthemcalories.core.rx.AbstractRxDatabaseModel;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientDao;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao;
import com.github.st1hy.countthemcalories.database.MealDao;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import org.greenrobot.greendao.query.CursorQuery;
import org.joda.time.DateTime;

import javax.inject.Inject;

import dagger.Lazy;
import rx.Observable;

@PerFragment
public class GraphData extends AbstractRxDatabaseModel {

    private CursorQuery query;
    @Inject
    private PhysicalQuantitiesModel quanityModel;

    @Inject
    public GraphData(@NonNull Lazy<DaoSession> session) {
        super(session);
    }

    @NonNull
    @CheckResult
    public Observable<GraphDayCursor> loadData(@NonNull final DateTime start, @NonNull final DateTime end) {
        return fromDatabaseTask(() -> {
            CursorQuery query = dataQuery().forCurrentThread();
            query.setParameter(0, start.getMillis());
            query.setParameter(1, end.getMillis());
            return new GraphDayCursor(query.query(), quanityModel, start, end);
        });
    }

    @NonNull
    private CursorQuery dataQuery() {
        if (query == null) {
            StringBuilder sql = new StringBuilder(512);
            sql.append("SELECT ");
            sql.append("M.").append(MealDao.Properties.CreationDate.columnName).append(", ");
            sql.append("I.").append(IngredientDao.Properties.Amount.columnName).append(", ");
            sql.append("IT.").append(IngredientTemplateDao.Properties.EnergyDensityAmount.columnName).append(", ");
            sql.append("IT.").append(IngredientTemplateDao.Properties.AmountType.columnName).append(" ");
            sql.append("from ").append(MealDao.TABLENAME).append(" M ");
            sql.append("join ").append(IngredientDao.TABLENAME).append(" I ");
            sql.append("on I.").append(IngredientDao.Properties.PartOfMealId.columnName)
                    .append(" = ").append(MealDao.Properties.Id.columnName).append(" ");
            sql.append("join ").append(IngredientTemplateDao.TABLENAME).append(" IT ");
            sql.append("on ").append(IngredientDao.Properties.IngredientTypeId.columnName)
                    .append(" = ").append(IngredientTemplateDao.Properties.Id.columnName).append(" ");
            sql.append("where M.").append(MealDao.Properties.CreationDate.columnName).append(" ");
            sql.append("between ").append("(?) and (?) ");
            sql.append("order by M.").append(MealDao.Properties.CreationDate.columnName).append(" asc;");
            query = CursorQuery.internalCreate(dao(), sql.toString(), new Object[2]);
        }
        return query;
    }

    @NonNull
    private MealDao dao() {
        return session().getMealDao();
    }

}
