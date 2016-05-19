package com.github.st1hy.countthemcalories.activities.addmeal.model;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.Unit;

import java.math.BigDecimal;

import javax.inject.Inject;

import rx.functions.Func1;

public class PhysicalQuantitiesModel {
    final SettingsModel settingsModel;
    final Resources resources;

    @Inject
    public PhysicalQuantitiesModel(@NonNull SettingsModel settingsModel, @NonNull Resources resources) {
        this.settingsModel = settingsModel;
        this.resources = resources;
    }

    @NonNull
    public EnergyDensity convertToPreferred(@NonNull EnergyDensity energyDensity) {
        AmountUnit preferredAmountUnit = settingsModel.getAmountUnitFrom(energyDensity.getAmountUnitType());
        EnergyUnit preferredEnergyUnit = settingsModel.getEnergyUnit();
        return energyDensity.convertTo(preferredEnergyUnit, preferredAmountUnit);
    }

    @NonNull
    public String format(@NonNull EnergyDensity energyDensity) {
        String energyUnit = resources.getString(energyDensity.getEnergyUnit().getNameRes());
        String amountUnit = resources.getString(energyDensity.getAmountUnit().getNameRes());
        String value = energyDensity.getValue()
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
        return resources.getString(R.string.format_value_fraction, value, energyUnit, amountUnit);
    }

    @NonNull
    public String convertAndFormat(@NonNull EnergyDensity energyDensity) {
        return format(convertToPreferred(energyDensity));
    }

    @NonNull
    public String format(@NonNull BigDecimal amount, @NonNull Unit unit) {
        return resources.getString(R.string.format_value_simple, amount.toPlainString(), getUnitName(unit));
    }

    @NonNull
    public String getUnitName(@NonNull Unit unit) {
        return settingsModel.getUnitName(unit);
    }

    /**
     * Calculates energy from amount and energy density
     *
     * @param amount        how much substance it is
     * @param amountUnit    what unit is the amount in
     * @param energyDensity what is its energy density
     * @return amount of energy in unit of {@link EnergyDensity#getEnergyUnit()}
     */
    @NonNull
    public BigDecimal getEnergyAmountFrom(@NonNull BigDecimal amount, @NonNull AmountUnit amountUnit,
                                          @NonNull EnergyDensity energyDensity) {
        return energyDensity.getValue()
                .multiply(amount)
                .multiply(amountUnit.getBase())
                .divide(energyDensity.getAmountUnit().getBase(), EnergyDensityUtils.DEFAULT_PRECISION)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros();
    }

    /**
     * Calculates and formats energy from amount and energy density
     *
     * @param amount        how much substance it is
     * @param amountUnit    what unit is the amount in
     * @param energyDensity what is its energy density
     * @return formatted value of energy
     */
    @NonNull
    public String formatEnergyCount(@NonNull BigDecimal amount, @NonNull AmountUnit amountUnit,
                                    @NonNull EnergyDensity energyDensity) {
        BigDecimal energyAmount = getEnergyAmountFrom(amount, amountUnit, energyDensity);
        return format(energyAmount, energyDensity.getEnergyUnit());
    }

    @NonNull
    public BigDecimal convertAmountFromDatabase(@NonNull BigDecimal databaseAmount,
                                                @NonNull AmountUnit targetUnit) {
        AmountUnit databaseUnit = EnergyDensityUtils.getDefaultAmountUnit(targetUnit.getType());
        return convertAmount(databaseAmount, databaseUnit, targetUnit);
    }

    @NonNull
    public BigDecimal convertAmountToDatabase(@NonNull BigDecimal sourceAmount,
                                              @NonNull AmountUnit sourceUnit) {
        AmountUnit databaseUnit = EnergyDensityUtils.getDefaultAmountUnit(sourceUnit.getType());
        return convertAmount(sourceAmount, sourceUnit, databaseUnit);
    }

    @NonNull
    public BigDecimal convertAmount(@NonNull BigDecimal amount,
                                    @NonNull AmountUnit from,
                                    @NonNull AmountUnit to) {
        return amount.multiply(to.getBase())
                .divide(from.getBase(), EnergyDensityUtils.DEFAULT_PRECISION)
                .stripTrailingZeros();
    }

    @NonNull
    public SettingsModel getSettingsModel() {
        return settingsModel;
    }

    @NonNull
    public Func1<Ingredient, BigDecimal> mapToEnergy() {
        return new Func1<Ingredient, BigDecimal>() {
            @Override
            public BigDecimal call(Ingredient ingredient) {
                EnergyDensity databaseEnergyDensity = EnergyDensity.from(ingredient.getIngredientType());
                EnergyDensity energyDensity = convertToPreferred(databaseEnergyDensity);
                AmountUnit amountUnit = energyDensity.getAmountUnit().getBaseUnit();
                return getEnergyAmountFrom(ingredient.getAmount(), amountUnit, energyDensity);
            }
        };
    }

    @NonNull
    public Func1<BigDecimal, String> energyAsString() {
        return new Func1<BigDecimal, String>() {
            @Override
            public String call(BigDecimal decimal) {
                return format(decimal, settingsModel.getEnergyUnit());
            }
        };
    }

    /**
     * @return function that creates moving sum on each element
     */
    @NonNull
    public Func1<BigDecimal, BigDecimal> sumAll() {
        return new Func1<BigDecimal, BigDecimal>() {
            BigDecimal sum = BigDecimal.ZERO;

            @Override
            public BigDecimal call(BigDecimal decimal) {
                sum = sum.add(decimal);
                return sum;
            }
        };
    }

    @NonNull
    public Func1<BigDecimal, BigDecimal> setScale(final int scale) {
        return new Func1<BigDecimal, BigDecimal>() {
            @Override
            public BigDecimal call(BigDecimal decimal) {
                return decimal.setScale(scale, BigDecimal.ROUND_HALF_UP);
            }
        };
    }
}
