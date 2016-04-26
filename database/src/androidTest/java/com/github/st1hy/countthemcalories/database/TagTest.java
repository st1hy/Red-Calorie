package com.github.st1hy.countthemcalories.database;

import de.greenrobot.dao.test.AbstractDaoTestLongPk;

public class TagTest extends AbstractDaoTestLongPk<TagDao, Tag> {

    public TagTest() {
        super(TagDao.class);
    }

    int i = 0;

    @Override
    protected Tag createEntity(Long key) {
        Tag entity = new Tag();
        entity.setId(key);
        entity.setName("Test Name" + i++);
        return entity;
    }

}
