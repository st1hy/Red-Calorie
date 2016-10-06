package com.github.st1hy.countthemcalories.database;

import org.greenrobot.greendao.test.AbstractDaoTestLongPk;

import java.math.BigDecimal;

public class IngredientTest extends AbstractDaoTestLongPk<IngredientDao, Ingredient> {

    public IngredientTest() {
        super(IngredientDao.class);
    }

    @Override
    protected Ingredient createEntity(Long key) {
        Ingredient entity = new Ingredient();
        entity.setId(key);
        entity.setAmount(new BigDecimal("200"));
        return entity;
    }

}
