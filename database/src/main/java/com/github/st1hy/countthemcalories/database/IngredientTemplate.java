package com.github.st1hy.countthemcalories.database;

import android.net.Uri;

import com.github.st1hy.countthemcalories.database.property.AmountUnitTypePropertyConverter;
import com.github.st1hy.countthemcalories.database.property.BigDecimalPropertyConverter;
import com.github.st1hy.countthemcalories.database.property.JodaTimePropertyConverter;
import com.github.st1hy.countthemcalories.database.property.UriPropertyConverter;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

@Entity(active = true, nameInDb = "INGREDIENTS_TEMPLATE")
public class IngredientTemplate {

    @Id(autoincrement = true)
    @Index(unique = true)
    private Long id;

    @NotNull
    @Index
    private String name;

    @Convert(converter = UriPropertyConverter.class, columnType = String.class)
    private Uri imageUri;

    @Convert(converter = JodaTimePropertyConverter.class, columnType = long.class)
    @NotNull
    private DateTime creationDate;

    @Convert(converter = AmountUnitTypePropertyConverter.class, columnType = int.class)
    @NotNull
    private AmountUnitType amountType;

    @Convert(converter = BigDecimalPropertyConverter.class, columnType = String.class)
    @NotNull
    private BigDecimal energyDensityAmount;

    @ToMany(joinProperties = {
        @JoinProperty(name = "id", referencedName = "ingredientTypeId")
    })
    private List<Ingredient> childIngredients;

    @ToMany(joinProperties = {
        @JoinProperty(name = "id", referencedName = "ingredientTypeId")
    })
    private List<JointIngredientTag> tags;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1676194412)
    private transient IngredientTemplateDao myDao;

    public IngredientTemplate(Long id) {
        this.id = id;
    }

    @Generated(hash = 1263166216)
    public IngredientTemplate(Long id, @NotNull String name, Uri imageUri,
            @NotNull DateTime creationDate, @NotNull AmountUnitType amountType,
            @NotNull BigDecimal energyDensityAmount) {
        this.id = id;
        this.name = name;
        this.imageUri = imageUri;
        this.creationDate = creationDate;
        this.amountType = amountType;
        this.energyDensityAmount = energyDensityAmount;
    }

    @Generated(hash = 1849966788)
    public IngredientTemplate() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    @NotNull
    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(@NotNull DateTime creationDate) {
        this.creationDate = creationDate;
    }

    @NotNull
    public AmountUnitType getAmountType() {
        return amountType;
    }

    public void setAmountType(@NotNull AmountUnitType amountType) {
        this.amountType = amountType;
    }

    @NotNull
    public BigDecimal getEnergyDensityAmount() {
        return energyDensityAmount;
    }

    public void setEnergyDensityAmount(@NotNull BigDecimal energyDensityAmount) {
        this.energyDensityAmount = energyDensityAmount;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1786169411)
    public List<Ingredient> getChildIngredients() {
        if (childIngredients == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            IngredientDao targetDao = daoSession.getIngredientDao();
            List<Ingredient> childIngredientsNew = targetDao
                    ._queryIngredientTemplate_ChildIngredients(id);
            synchronized (this) {
                if (childIngredients == null) {
                    childIngredients = childIngredientsNew;
                }
            }
        }
        return childIngredients;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1103824659)
    public synchronized void resetChildIngredients() {
        childIngredients = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 97163663)
    public List<JointIngredientTag> getTags() {
        if (tags == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            JointIngredientTagDao targetDao = daoSession.getJointIngredientTagDao();
            List<JointIngredientTag> tagsNew = targetDao._queryIngredientTemplate_Tags(id);
            synchronized (this) {
                if (tags == null) {
                    tags = tagsNew;
                }
            }
        }
        return tags;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 404234)
    public synchronized void resetTags() {
        tags = null;
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
    @Generated(hash = 403714479)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getIngredientTemplateDao() : null;
    }

}
