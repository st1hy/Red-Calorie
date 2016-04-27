package com.github.st1hy.countthemcalories.database;

import org.joda.time.DateTime;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class MealTest extends AbstractDaoTestLongPk<MealDao, Meal> {

    public MealTest() {
        super(MealDao.class);
    }

    @Override
    protected Meal createEntity(Long key) {
        Meal entity = new Meal();
        entity.setId(key);
        entity.setCreationDate(DateTime.now());
        return entity;
    }

}