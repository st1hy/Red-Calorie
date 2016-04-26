package com.github.st1hy.countthemcalories.database;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class JointIngredientTagTest extends AbstractDaoTestLongPk<JointIngredientTagDao, JointIngredientTag> {

    public JointIngredientTagTest() {
        super(JointIngredientTagDao.class);
    }

    @Override
    protected JointIngredientTag createEntity(Long key) {
        JointIngredientTag entity = new JointIngredientTag();
        entity.setId(key);
        entity.setTagId(1);
        entity.setIngredientTypeId(1);
        return entity;
    }

}
