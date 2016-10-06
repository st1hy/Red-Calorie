package com.github.st1hy.countthemcalories.database.property;

import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;

import org.greenrobot.greendao.converter.PropertyConverter;

public class AmountUnitTypePropertyConverter implements PropertyConverter<AmountUnitType, Integer> {

    @Override
    public AmountUnitType convertToEntityProperty(Integer databaseValue) {
        return AmountUnitType.fromId(databaseValue);
    }

    @Override
    public Integer convertToDatabaseValue(AmountUnitType entityProperty) {
        return entityProperty.getId();
    }

}
