package com.github.st1hy.countthemcalories.database.property;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.GravimetricEnergyDensityUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumetricEnergyDensityUnit;

import java.math.BigDecimal;

import de.greenrobot.dao.converter.PropertyConverter;

public class EnergyDensityPropertyConverter implements PropertyConverter<EnergyDensity, String> {
    private static final String separator = "@"; //keep forever

    @Override
    @Nullable
    public EnergyDensity convertToEntityProperty(@Nullable String databaseValue) {
        if (databaseValue == null)
            return null;
        else {
            String[] split = databaseValue.split(separator);
            AmountUnitType unitType = AmountUnitType.fromId(Integer.valueOf(split[0]));
            EnergyDensityUnit standardizedUnit = getStandardizedUnit(unitType);
            BigDecimal value = new BigDecimal(split[1]);
            return new EnergyDensity(standardizedUnit, value);
        }
    }

    @Override
    @Nullable
    public String convertToDatabaseValue(@Nullable EnergyDensity entityProperty) {
        if (entityProperty == null)
            return null;
        else {
            final AmountUnitType amountUnitType = entityProperty.getAmountUnitType();
            final EnergyDensityUnit standardUnit = getStandardizedUnit(amountUnitType);
            return String.valueOf(amountUnitType.getId()) +
                    separator +
                    entityProperty.convertTo(standardUnit).getValue().toPlainString();
        }
    }

    @NonNull
    static EnergyDensityUnit getStandardizedUnit(@NonNull AmountUnitType amountUnitType) {
        switch (amountUnitType) {
            case VOLUME:
                return VolumetricEnergyDensityUnit.KJ_AT_ML;
            case MASS:
                return GravimetricEnergyDensityUnit.KJ_AT_G;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
