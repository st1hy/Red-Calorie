package com.github.st1hy.countthemcalories.database.parcel;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.Tag;

public class TagParcel extends DaoParcel<Tag> {
    final long id;

    public TagParcel(@NonNull Tag dao) {
        super(dao);
        this.id = dao.getId();
    }

    protected TagParcel(long id, @NonNull ReadFromDb<Tag> readDb) {
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

    public static Creator<TagParcel> CREATOR = new Creator<TagParcel>() {
        @Override
        public TagParcel createFromParcel(Parcel source) {
            long id = source.readLong();
            return new TagParcel(id, new ReadFromDb<Tag>(id) {
                @Override
                protected SessionReader<Tag> readDao(@NonNull DaoSession session, long id) {
                    return new SessionReader<Tag>(session, Tag.class, id) {
                        @Override
                        public Tag call() throws Exception {
                            Tag tag = super.call();
                            tag.getIngredientTypes();
                            //TODO check if how deep to load
                            return tag;
                        }
                    };
                }
            });
        }

        @Override
        public TagParcel[] newArray(int size) {
            return new TagParcel[size];
        }
    };
}
