package com.github.st1hy.countthemcalories.database;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.parceler.Parcel;

@Entity(active = true, nameInDb = "INGREDIENT_TAG_JOINTS")
@Parcel
public class JointIngredientTag {

    @Id(autoincrement = true)
    @Index(unique = true)
    Long id;

    @Index
    long tagId;

    @Index
    long ingredientTypeId;

    @ToOne(joinProperty = "tagId")
    Tag tag;

    @ToOne(joinProperty = "ingredientTypeId")
    IngredientTemplate ingredientType;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1685847810)
    private transient JointIngredientTagDao myDao;

    @Generated(hash = 1006483784)
    private transient Long tag__resolvedKey;

    @Generated(hash = 968189967)
    private transient Long ingredientType__resolvedKey;

    public JointIngredientTag(Long id) {
        this.id = id;
    }

    @Generated(hash = 408327599)
    public JointIngredientTag(Long id, long tagId, long ingredientTypeId) {
        this.id = id;
        this.tagId = tagId;
        this.ingredientTypeId = ingredientTypeId;
    }

    @Generated(hash = 373535361)
    public JointIngredientTag() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public long getIngredientTypeId() {
        return ingredientTypeId;
    }

    public void setIngredientTypeId(long ingredientTypeId) {
        this.ingredientTypeId = ingredientTypeId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1607739560)
    public Tag getTag() {
        long __key = this.tagId;
        if (tag__resolvedKey == null || !tag__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TagDao targetDao = daoSession.getTagDao();
            Tag tagNew = targetDao.load(__key);
            synchronized (this) {
                tag = tagNew;
                tag__resolvedKey = __key;
            }
        }
        return tag;
    }

    public Tag getTagOrNull() {
        return tag;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1728988431)
    public void setTag(@NotNull Tag tag) {
        if (tag == null) {
            throw new DaoException(
                    "To-one property 'tagId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.tag = tag;
            tagId = tag.getId();
            tag__resolvedKey = tagId;
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
    @Generated(hash = 371558487)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getJointIngredientTagDao() : null;
    }

}
