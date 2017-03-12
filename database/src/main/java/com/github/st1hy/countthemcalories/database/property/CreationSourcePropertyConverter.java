package com.github.st1hy.countthemcalories.database.property;

import org.greenrobot.greendao.converter.PropertyConverter;

public class CreationSourcePropertyConverter implements PropertyConverter<CreationSource, Integer> {

    @Override
    public CreationSource convertToEntityProperty(Integer databaseValue) {
        return CreationSource.fromDatabaseValue(databaseValue);
    }

    @Override
    public Integer convertToDatabaseValue(CreationSource entityProperty) {
        return entityProperty.getDatabaseValue();
    }
}
