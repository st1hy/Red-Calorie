package com.github.st1hy.countthemcalories.database;

import org.joda.time.DateTime;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class DayTest extends AbstractDaoTestLongPk<DayDao, Day> {

    public DayTest() {
        super(DayDao.class);
    }

    int newDays = 0;

    @Override
    protected Day createEntity(Long key) {
        Day entity = new Day();
        entity.setId(key);
        //each day must be unique
        DateTime now = DateTime.now().plusDays(newDays++);
        entity.setDate(now);
        return entity;
    }

}
