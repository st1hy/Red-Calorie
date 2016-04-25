package com.github.st1hy.countthemcalories.database.property;

import android.net.Uri;
import android.support.annotation.Nullable;

import de.greenrobot.dao.converter.PropertyConverter;

public class UriPropertyConverter implements PropertyConverter<Uri, String> {

    @Override
    @Nullable
    public Uri convertToEntityProperty(@Nullable String databaseValue) {
        if (databaseValue == null)
            return null;
        else {
            return Uri.parse(databaseValue);
        }
    }

    @Override
    @Nullable
    public String convertToDatabaseValue(Uri entityProperty) {
        if (entityProperty == null)
            return null;
        else {
            return entityProperty.toString();
        }
    }
}
