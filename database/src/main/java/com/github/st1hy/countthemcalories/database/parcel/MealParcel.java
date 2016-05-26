package com.github.st1hy.countthemcalories.database.parcel;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.Meal;

import java.util.List;

public class MealParcel extends DaoParcel<Meal> {
    final long id;

    public MealParcel(@NonNull Meal dao) {
        super(dao);
        this.id = dao.getId();
    }

    protected MealParcel(long id, @NonNull ReadFromDb<Meal> readDb) {
        super(readDb);
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
    }

    public static Creator<MealParcel> CREATOR = new Creator<MealParcel>() {
        @Override
        public MealParcel createFromParcel(Parcel source) {
            long id = source.readLong();
            return new MealParcel(id, new ReadFromDb<Meal>(id) {
                @Override
                protected SessionReader<Meal> readDao(@NonNull DaoSession session, long id) {
                    return new SessionReader<Meal>(session, Meal.class, id) {
                        @Override
                        public Meal call() throws Exception {
                            Meal meal = super.call();
                            List<Ingredient> ingredients = meal.getIngredients();
                            for (Ingredient ingredient : ingredients) {
                                ingredient.getIngredientType();
                            }
                            return meal;
                        }
                    };
                }
            });
        }

        @Override
        public MealParcel[] newArray(int size) {
            return new MealParcel[size];
        }
    };

}
