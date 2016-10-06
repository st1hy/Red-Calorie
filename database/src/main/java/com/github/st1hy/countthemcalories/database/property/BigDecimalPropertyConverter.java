package com.github.st1hy.countthemcalories.database.property;

import android.support.annotation.Nullable;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.math.BigDecimal;

public class BigDecimalPropertyConverter implements PropertyConverter<BigDecimal, String> {

    @Override
    @Nullable
    public BigDecimal convertToEntityProperty(@Nullable String databaseValue) {
        if (databaseValue == null)
            return null;
        else return new BigDecimal(databaseValue);
    }

    @Override
    @Nullable
    public String convertToDatabaseValue(@Nullable BigDecimal entityProperty) {
        if (entityProperty == null)
            return null;
        else return entityProperty.toPlainString();
    }
}
