package com.github.st1hy.countthemcalories.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.ToOne;

@Entity(active = true, nameInDb = "INGREDIENT_TAG_JOINTS")
public class JointIngredientTag {

    @Id(autoincrement = true)
    @Index(unique = true)
    private Long id;

    @Index
    private long tagId;

    @Index
    private long ingredientTypeId;

    @ToOne(joinProperty = "tagId")
    private Tag tag;

    @ToOne(joinProperty = "ingredientTypeId")
    private IngredientTemplate ingredientType;

    public JointIngredientTag(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

    public long getIngredientTypeId() {
        return ingredientTypeId;
    }

    public void setIngredientTypeId(long ingredientTypeId) {
        this.ingredientTypeId = ingredientTypeId;
    }

}
