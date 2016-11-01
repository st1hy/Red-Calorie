package com.github.st1hy.countthemcalories.activities.addingredient.fragment.model;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnit;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUtils;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.MassUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumeUnit;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class AddIngredientModelHelper {

    private final SettingsModel settingsModel;
    private final IngredientTagsModel tagsModel;
    private final RxIngredientsDatabaseModel databaseModel;
    private final Resources resources;

    private final AddIngredientModel model;

    @Inject
    @PerFragment
    public AddIngredientModelHelper(@NonNull SettingsModel settingsModel,
                                    @NonNull IngredientTagsModel tagsModel,
                                    @NonNull RxIngredientsDatabaseModel databaseModel,
                                    @NonNull Resources resources,
                                    @NonNull AddIngredientModel model) {
        this.settingsModel = settingsModel;
        this.tagsModel = tagsModel;
        this.databaseModel = databaseModel;
        this.resources = resources;
        this.model = model;
    }

    @NonNull
    public String getEnergyDensityUnit() {
        String energy = settingsModel.getUnitName(getEnergyUnit());
        String amount = settingsModel.getUnitName(getAmountUnit());
        return energyDensityUnitOf(energy, amount);
    }

    @NonNull
    private AmountUnit getAmountUnit() {
        AmountUnitType amountType = model.getAmountType();
        return settingsModel.getAmountUnitFrom(amountType);
    }

    @NonNull
    private EnergyUnit getEnergyUnit() {
        return settingsModel.getEnergyUnit();
    }

    @NonNull
    private String energyDensityUnitOf(String energyUnit, String amountUnit) {
        return resources.getString(R.string.format_value_fraction, "", energyUnit, amountUnit)
                .trim();
    }

    @NonNull
    public List<Pair<AmountUnit, CharSequence>> getSelectTypeDialogOptions() {
        String energy = settingsModel.getUnitName(getEnergyUnit());
        MassUnit massUnit = settingsModel.getMassUnit();
        String mass = settingsModel.getUnitName(massUnit);
        VolumeUnit volumeUnit = settingsModel.getVolumeUnit();
        String volume = settingsModel.getUnitName(volumeUnit);
        List<Pair<AmountUnit, CharSequence>> list = Lists.newLinkedList();
        list.add(Pair.<AmountUnit, CharSequence>create(massUnit, energyDensityUnitOf(energy, mass)));
        list.add(Pair.<AmountUnit, CharSequence>create(volumeUnit, energyDensityUnitOf(energy, volume)));
        return list;
    }


    /**
     * @return observable ingredient that was added or updated in database OR {@link IngredientTypeCreateException}
     * if not data is in incorrect state to be saved
     */
    @NonNull
    public Observable<IngredientTemplate> saveIntoDatabase() {
        List<IngredientTypeCreateException.ErrorType> errorList = canCreateIngredient();
        if (errorList.isEmpty()) {
            return insertOrUpdateIntoDatabase();
        } else {
            return Observable.error(new IngredientTypeCreateException(errorList));
        }
    }

    @NonNull
    private Observable<IngredientTemplate> insertOrUpdateIntoDatabase() {
        return databaseModel.insertOrUpdate(intoTemplate(), tagsModel.getTagIds());
    }

    @NonNull
    private IngredientTemplate intoTemplate() {
        IngredientTemplate template = new IngredientTemplate();
        template.setName(model.getName());
        template.setImageUri(model.getImageUri());
        template.setCreationDate(model.getCreationDate());
        template.setAmountType(model.getAmountType());
        template.setEnergyDensityAmount(getEnergyDensity().convertToDatabaseFormat().getValue());
        return template;
    }


    @NonNull
    private EnergyDensity getEnergyDensity() {
        return getEnergyDensity(model.getEnergyValue());
    }

    @NonNull
    private EnergyDensity getEnergyDensity(@NonNull String value) {
        return EnergyDensityUtils.getOrZero(getEnergyUnit(), getAmountUnit(), value);
    }

    @NonNull
    List<IngredientTypeCreateException.ErrorType> canCreateIngredient() {
        return canCreateIngredient(model.getName(), model.getEnergyValue());
    }

    @NonNull
    public List<IngredientTypeCreateException.ErrorType> canCreateIngredient(@NonNull String name, @NonNull String value) {
        List<IngredientTypeCreateException.ErrorType> errors = new ArrayList<>(4);
        if (isEmpty(name)) errors.add(IngredientTypeCreateException.ErrorType.NO_NAME);
        if (isEmpty(value)) errors.add(IngredientTypeCreateException.ErrorType.NO_VALUE);
        else if (!isValueGreaterThanZero(value))
            errors.add(IngredientTypeCreateException.ErrorType.ZERO_VALUE);
        return errors;
    }


    private static boolean isEmpty(@NonNull String name) {
        return name.trim().isEmpty();
    }

    private boolean isValueGreaterThanZero(@NonNull String value) {
        return getEnergyDensity(value).getValue().compareTo(BigDecimal.ZERO) > 0;
    }

    @NonNull
    public String convertEnergyDensityToEnergyValueString(@NonNull AmountUnitType amountUnitType, @NonNull BigDecimal energyDensityAmount) {
        return EnergyDensity.fromDatabaseValue(amountUnitType, energyDensityAmount)
                .convertTo(getEnergyUnit(), getAmountUnit())
                .getValue()
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    public Uri getSearchIngredientQuery(@NonNull String name) {
        String search = (name.trim() + "+" + resources.getString(R.string.add_ingredient_default_search_keywords))
                .trim().replace(" ", "+");
        return Uri.parse(resources.getString(R.string.default_search_query, search));
    }

}
