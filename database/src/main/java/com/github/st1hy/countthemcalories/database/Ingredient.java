package com.github.st1hy.countthemcalories.database;

import com.github.st1hy.countthemcalories.database.property.BigDecimalPropertyConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

import java.math.BigDecimal;

@Entity(active = true, nameInDb = "INGREDIENTS")
public class Ingredient {

    @Id(autoincrement = true)
    @Index(unique =  true)
    private Long id;

    @Convert(converter = BigDecimalPropertyConverter.class, columnType = String.class)
    @NotNull
    private BigDecimal amount;
    private long partOfMealId;
    private long ingredientTypeId;

    @ToOne(joinProperty = "partOfMealId")
    private Meal partOfMeal;

    @ToOne(joinProperty = "ingredientTypeId")
    private IngredientTemplate ingredientType;

    public Ingredient(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull BigDecimal amount) {
        this.amount = amount;
    }

}
