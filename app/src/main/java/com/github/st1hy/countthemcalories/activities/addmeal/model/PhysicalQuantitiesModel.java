package com.github.st1hy.countthemcalories.activities.addmeal.model;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.Unit;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.inject.Named;

import rx.functions.Func1;

public class PhysicalQuantitiesModel {
    private final SettingsModel settingsModel;
    private final Resources resources;
    private final Context context;
    private Func1<Ingredient, BigDecimal> ingredientToEnergy;
    private Func1<BigDecimal, String> energyAsString;

    @Inject
    public PhysicalQuantitiesModel(@NonNull final SettingsModel settingsModel,
                                   @NonNull @Named("appContext") Context context) {
        this.settingsModel = settingsModel;
        this.context = context;
        this.resources = context.getResources();
    }

    @NonNull
    public EnergyDensity convertToPreferred(@NonNull EnergyDensity energyDensity) {
        AmountUnit preferredAmountUnit = settingsModel.getAmountUnitFrom(energyDensity.getAmountUnitType());
        EnergyUnit preferredEnergyUnit = settingsModel.getEnergyUnit();
        return energyDensity.convertTo(preferredEnergyUnit, preferredAmountUnit);
    }

    /**
     * Formats energy density as "{value} {energy_unit} / {amount_unit}"
     */
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

    /**
     * Converts energy density to preferred units and formats it.
     * <p/>
     * Internally calls {@link #format(EnergyDensity)}.
     */
    @NonNull
    public String convertAndFormat(@NonNull EnergyDensity energyDensity) {
        return format(convertToPreferred(energyDensity));
    }

    /**
     * Formats amount as "{amount} {unit_name}"
     */
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

    /**
     * Convenience method for calling {@link #convertAmount(BigDecimal, AmountUnit, AmountUnit)}
     * with "from" argument that's default database unit for current type.
     * <p/>
     * Internally it uses {@link EnergyDensityUtils#getDefaultAmountUnit(AmountUnitType)} for resolving
     * current unit of provided value
     */
    @NonNull
    public BigDecimal convertAmountFromDatabase(@NonNull BigDecimal databaseAmount,
                                                @NonNull AmountUnit targetUnit) {
        AmountUnit databaseUnit = EnergyDensityUtils.getDefaultAmountUnit(targetUnit.getType());
        return convertAmount(databaseAmount, databaseUnit, targetUnit);
    }

    /**
     * Convenience method for calling {@link #convertAmount(BigDecimal, AmountUnit, AmountUnit)}
     * with "to" argument that's default database unit for current type.
     * <p/>
     * Internally it uses {@link EnergyDensityUtils#getDefaultAmountUnit(AmountUnitType)} for resolving
     * target unit of provided value
     */
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
        return amount.multiply(from.getBase())
                .divide(to.getBase(), EnergyDensityUtils.DEFAULT_PRECISION)
                .stripTrailingZeros();
    }

    @NonNull
    public SettingsModel getSettingsModel() {
        return settingsModel;
    }

    @NonNull
    public Func1<Ingredient, BigDecimal> mapToEnergy() {
        if (ingredientToEnergy == null) {
            ingredientToEnergy = new Func1<Ingredient, BigDecimal>() {
                @Override
                public BigDecimal call(Ingredient ingredient) {
                    IngredientTemplate ingredientTemplate = ingredient.getIngredientTypeOrNull();
                    EnergyDensity databaseEnergyDensity = EnergyDensity.from(ingredientTemplate);
                    EnergyDensity energyDensity = convertToPreferred(databaseEnergyDensity);
                    AmountUnit amountUnit = EnergyDensityUtils.getDefaultAmountUnit(ingredientTemplate.getAmountType());
                    return getEnergyAmountFrom(ingredient.getAmount(), amountUnit, energyDensity);
                }
            };
        }
        return ingredientToEnergy;
    }

    @NonNull
    public Func1<BigDecimal, String> energyAsString() {
        if (energyAsString == null) {
            energyAsString = new Func1<BigDecimal, String>() {
                @Override
                public String call(BigDecimal decimal) {
                    return format(decimal, settingsModel.getEnergyUnit());
                }
            };
        }
        return energyAsString;
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

    @NonNull
    public String formatTime(@NonNull DateTime date) {
        if (DateFormat.is24HourFormat(context)) {
            return DateTimeFormat.forPattern("HH:mm").print(date);
        } else {
            return DateTimeFormat.forPattern("hh:mm aa").print(date);
        }
    }
}
