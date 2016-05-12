package com.github.st1hy.countthemcalories.database.parcel;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;

public class IngredientTypeParcel extends DaoParcel<IngredientTemplate> {
    final long id;

    public IngredientTypeParcel(@NonNull IngredientTemplate dao) {
        super(dao);
        this.id = dao.getId();
    }

    protected IngredientTypeParcel(long id, @NonNull ReadFromDb<IngredientTemplate> readDb) {
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

    public static Creator<IngredientTypeParcel> CREATOR = new Creator<IngredientTypeParcel>() {
        @Override
        public IngredientTypeParcel createFromParcel(Parcel source) {
            long id = source.readLong();
            return new IngredientTypeParcel(id, new ReadFromDb<IngredientTemplate>(id) {
                @Override
                protected SessionReader<IngredientTemplate> readDao(@NonNull DaoSession session, long id) {
                    return new SessionReader<>(session, IngredientTemplate.class, id);
                }
            });
        }

        @Override
        public IngredientTypeParcel[] newArray(int size) {
            return new IngredientTypeParcel[size];
        }
    };

}
