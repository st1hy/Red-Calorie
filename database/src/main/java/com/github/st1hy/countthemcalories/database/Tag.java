package com.github.st1hy.countthemcalories.database;

import com.github.st1hy.countthemcalories.database.property.CreationSource;
import com.github.st1hy.countthemcalories.database.property.CreationSourcePropertyConverter;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Transient;
import org.parceler.Parcel;

import java.util.List;

import javax.annotation.Nullable;

@Entity(active = true, nameInDb = "TAGS")
@Parcel
public class Tag {

    @Id(autoincrement = true)
    @Index(unique = true)
    Long id;

    @NotNull
    @Index(unique = true)
    String name;

    @Convert(converter = CreationSourcePropertyConverter.class, columnType = int.class)
    @NotNull
    @Index
    CreationSource creationSource;

    @ToMany(joinProperties = {
            @JoinProperty(name = "id", referencedName = "tagId")
    })
    List<IngredientTagJoint> ingredientTypes;

    @Transient
    I18n translations;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 2076396065)
    private transient TagDao myDao;

    @Transient
    int ingredientCount = -1;

    public Tag(Long id) {
        this.id = id;
    }

    @Generated(hash = 1605720318)
    public Tag() {
    }

    public Tag(Tag tag) {
        this.id = tag.id;
        this.name = tag.name;
        this.ingredientTypes = tag.ingredientTypes;
        this.daoSession = tag.daoSession;
        this.myDao = tag.myDao;
        this.ingredientCount = tag.ingredientCount;
        this.translations = tag.translations;
        this.creationSource = tag.creationSource;
    }

    public Tag(@Nullable Long id, @NotNull String name) {
        this(id, name, CreationSource.USER);
    }

    @Generated(hash = 1398420030)
    public Tag(Long id, @NotNull String name, @NotNull CreationSource creationSource) {
        this.id = id;
        this.name = name;
        this.creationSource = creationSource;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getIngredientCount() {
        return ingredientCount;
    }

    public void setIngredientCount(int ingredientCount) {
        this.ingredientCount = ingredientCount;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public I18n getTranslations() {
        return translations;
    }

    public void setTranslations(I18n translations) {
        this.translations = translations;
    }

    public String getDisplayName() {
        if (translations != null && creationSource == CreationSource.GENERATED) {
            return translations.getCurrent();
        }
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        return (id != null ? id.equals(tag.id) : tag.id == null)
                && (name != null ? name.equals(tag.name) : tag.name == null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 2143269227)
    public synchronized void resetIngredientTypes() {
        ingredientTypes = null;
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

    public CreationSource getCreationSource() {
        return this.creationSource;
    }

    public void setCreationSource(CreationSource creationSource) {
        this.creationSource = creationSource;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1839928301)
    public List<IngredientTagJoint> getIngredientTypes() {
        if (ingredientTypes == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            IngredientTagJointDao targetDao = daoSession.getIngredientTagJointDao();
            List<IngredientTagJoint> ingredientTypesNew = targetDao
                    ._queryTag_IngredientTypes(id);
            synchronized (this) {
                if (ingredientTypes == null) {
                    ingredientTypes = ingredientTypesNew;
                }
            }
        }
        return ingredientTypes;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 441429822)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTagDao() : null;
    }

}
