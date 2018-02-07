package com.github.st1hy.countthemcalories.database.property;

import android.support.annotation.Nullable;

import org.greenrobot.greendao.converter.PropertyConverter;
import org.joda.time.DateTime;

public class JodaTimePropertyConverter implements PropertyConverter<DateTime, Long> {

    @Override
    @Nullable
    public DateTime convertToEntityProperty(@Nullable Long databaseValue) {
        if (databaseValue == null)
            return null;
        else {
            return new DateTime(databaseValue);
        }
    }

    @Override
    @Nullable
    public Long convertToDatabaseValue(@Nullable DateTime entityProperty) {
        if (entityProperty == null)
            return null;
        else
            return entityProperty.getMillis();
    }
}
