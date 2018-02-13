package com.github.st1hy.countthemcalories.database;

import com.github.st1hy.countthemcalories.database.property.JodaTimePropertyConverter;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.joda.time.DateTime;
import org.parceler.Parcel;

@Entity(active = true, nameInDb = "WEIGTH")
@Parcel
public class Weight {

    @Id(autoincrement = true)
    @Index(unique = true)
    Long id;

    @Convert(converter = JodaTimePropertyConverter.class, columnType = long.class)
    @NotNull
    @Index(unique = true)
    DateTime measurementDate;

    float weight;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1611231079)
    private transient WeightDao myDao;

    @Generated(hash = 1956860650)
    public Weight() {
    }

    @Generated(hash = 1782361459)
    public Weight(Long id, @NotNull DateTime measurementDate, float weight) {
        this.id = id;
        this.measurementDate = measurementDate;
        this.weight = weight;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getWeight() {
        return weight;
    }

    public DateTime getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(DateTime measurementDate) {
        this.measurementDate = measurementDate;
    }

    @Override
    public String toString() {
        return "Weight{" +
                "weight=" + weight +
                ", measurementDate=" + measurementDate +
                ", id=" + id +
                '}';
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 736726916)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getWeightDao() : null;
    }
}
