package com.github.st1hy.countthemcalories.database;

import com.github.st1hy.countthemcalories.database.property.BigDecimalPropertyConverter;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.parceler.Parcel;

import java.math.BigDecimal;

@Entity(active = true, nameInDb = "INGREDIENTS")
@Parcel
public class Ingredient {

    @Id(autoincrement = true)
    @Index(unique =  true)
    Long id;

    @Convert(converter = BigDecimalPropertyConverter.class, columnType = String.class)
    @NotNull
    BigDecimal amount;
    long partOfMealId;
    long ingredientTypeId;

    @ToOne(joinProperty = "partOfMealId")
    Meal partOfMeal;

    @ToOne(joinProperty = "ingredientTypeId")
    IngredientTemplate ingredientType;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 942581853)
    private transient IngredientDao myDao;

    @Generated(hash = 1814803381)
    private transient Long partOfMeal__resolvedKey;

    @Generated(hash = 968189967)
    private transient Long ingredientType__resolvedKey;

    public Ingredient(Long id) {
        this.id = id;
    }

    @Generated(hash = 318402527)
    public Ingredient(Long id, @NotNull BigDecimal amount, long partOfMealId,
            long ingredientTypeId) {
        this.id = id;
        this.amount = amount;
        this.partOfMealId = partOfMealId;
        this.ingredientTypeId = ingredientTypeId;
    }

    @Generated(hash = 1584798654)
    public Ingredient() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull BigDecimal amount) {
        this.amount = amount;
    }

    public long getPartOfMealId() {
        return this.partOfMealId;
    }

    public void setPartOfMealId(long partOfMealId) {
        this.partOfMealId = partOfMealId;
    }

    public long getIngredientTypeId() {
        return this.ingredientTypeId;
    }

    public void setIngredientTypeId(long ingredientTypeId) {
        this.ingredientTypeId = ingredientTypeId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1124613321)
    public Meal getPartOfMeal() {
        long __key = this.partOfMealId;
        if (partOfMeal__resolvedKey == null || !partOfMeal__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MealDao targetDao = daoSession.getMealDao();
            Meal partOfMealNew = targetDao.load(__key);
            synchronized (this) {
                partOfMeal = partOfMealNew;
                partOfMeal__resolvedKey = __key;
            }
        }
        return partOfMeal;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 405677469)
    public void setPartOfMeal(@NotNull Meal partOfMeal) {
        if (partOfMeal == null) {
            throw new DaoException(
                    "To-one property 'partOfMealId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.partOfMeal = partOfMeal;
            partOfMealId = partOfMeal.getId();
            partOfMeal__resolvedKey = partOfMealId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 739336867)
    public IngredientTemplate getIngredientType() {
        long __key = this.ingredientTypeId;
        if (ingredientType__resolvedKey == null
                || !ingredientType__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            IngredientTemplateDao targetDao = daoSession.getIngredientTemplateDao();
            IngredientTemplate ingredientTypeNew = targetDao.load(__key);
            synchronized (this) {
                ingredientType = ingredientTypeNew;
                ingredientType__resolvedKey = __key;
            }
        }
        return ingredientType;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 654512060)
    public void setIngredientType(@NotNull IngredientTemplate ingredientType) {
        if (ingredientType == null) {
            throw new DaoException(
                    "To-one property 'ingredientTypeId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.ingredientType = ingredientType;
            ingredientTypeId = ingredientType.getId();
            ingredientType__resolvedKey = ingredientTypeId;
        }
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1386056592)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getIngredientDao() : null;
    }

}
