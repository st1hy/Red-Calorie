package com.github.st1hy.countthemcalories.database;

import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyUnit;
import com.github.st1hy.countthemcalories.database.unit.VolumeUnit;

import org.joda.time.DateTime;

import java.math.BigDecimal;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class IngredientTemplateTest extends AbstractDaoTestLongPk<IngredientTemplateDao, IngredientTemplate> {

    public IngredientTemplateTest() {
        super(IngredientTemplateDao.class);
    }

    @Override
    protected IngredientTemplate createEntity(Long key) {
        IngredientTemplate entity = new IngredientTemplate();
        entity.setId(key);
        entity.setName("Test ingredient");
        entity.setCreationDate(DateTime.now());
        entity.setAmountType(AmountUnitType.MASS);
        entity.setEnergyDensityAmount(new EnergyDensity(EnergyUnit.KCAL, VolumeUnit.ML100, BigDecimal.valueOf(23,1))
                .convertTo(EnergyUnit.KJ, VolumeUnit.ML).getValue());
        return entity;
    }

}
