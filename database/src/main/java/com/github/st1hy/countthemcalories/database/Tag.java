package com.github.st1hy.countthemcalories.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity(active = true, nameInDb = "TAGS")
public class Tag {

    @Id(autoincrement = true)
    @Index(unique = true)
    private Long id;

    @NotNull
    @Index(unique = true)
    private String name;

    @ToMany(joinProperties = {
        @JoinProperty(name = "id", referencedName = "tagId")
    })
    private List<JointIngredientTag> ingredientTypes;

    public Tag(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

}
