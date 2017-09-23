package com.github.st1hy.countthemcalories.database;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.property.JodaTimePropertyConverter;
import com.github.st1hy.countthemcalories.database.property.UriPropertyConverter;

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
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import dagger.internal.Preconditions;

@Entity(active = true, nameInDb = "MEALS")
@Parcel
public class Meal {

    @Id(autoincrement = true)
    @Index(unique = true)
    Long id;

    @Index
    String name;

    @Convert(converter = UriPropertyConverter.class, columnType = String.class)
    Uri imageUri;

    @Convert(converter = JodaTimePropertyConverter.class, columnType = long.class)
    @NotNull
    @Index
    DateTime creationDate;

    @ToMany(joinProperties = {
        @JoinProperty(name = "id", referencedName = "partOfMealId")
    })
    List<Ingredient> ingredients;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1947976862)
    private transient MealDao myDao;

    public Meal(Long id) {
        this.id = id;
    }

    @Generated(hash = 978964286)
    public Meal(Long id, String name, Uri imageUri, @NotNull DateTime creationDate) {
        this.id = id;
        this.name = name;
        this.imageUri = imageUri;
        this.creationDate = creationDate;
    }

    @Generated(hash = 167100247)
    public Meal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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

    public boolean hasIngredients() {
        return ingredients != null && !ingredients.isEmpty();
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1292705092)
    public List<Ingredient> getIngredients() {
        if (ingredients == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            IngredientDao targetDao = daoSession.getIngredientDao();
            List<Ingredient> ingredientsNew = targetDao._queryMeal_Ingredients(id);
            synchronized (this) {
                if (ingredients == null) {
                    ingredients = ingredientsNew;
                }
            }
        }
        return ingredients;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 183837919)
    public synchronized void resetIngredients() {
        ingredients = null;
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

    @NonNull
    public static Meal copyAsNew(@NonNull Meal meal) {
        Meal newMeal = new Meal();
        newMeal.name = "";
        newMeal.imageUri = meal.imageUri;
        List<Ingredient> ingredients = meal.getIngredients();
        List<Ingredient> newIngredients = new ArrayList<>(ingredients.size());
        for (Ingredient old : ingredients) {
            IngredientTemplate template = Preconditions.checkNotNull(old.getIngredientTypeOrNull());
            newIngredients.add(new Ingredient(template, old.getAmount()));
        }
        newMeal.ingredients = newIngredients;
        return newMeal;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 644317336)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMealDao() : null;
    }
}
