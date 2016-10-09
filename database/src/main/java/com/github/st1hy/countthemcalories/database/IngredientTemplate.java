package com.github.st1hy.countthemcalories.database;

import android.net.Uri;

import com.github.st1hy.countthemcalories.database.property.AmountUnitTypePropertyConverter;
import com.github.st1hy.countthemcalories.database.property.BigDecimalPropertyConverter;
import com.github.st1hy.countthemcalories.database.property.JodaTimePropertyConverter;
import com.github.st1hy.countthemcalories.database.property.UriPropertyConverter;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;

@Entity(active = true, nameInDb = "INGREDIENTS_TEMPLATE")
public class IngredientTemplate {

    @Id(autoincrement = true)
    @Index(unique = true)
    private Long id;

    @NotNull
    @Index
    private String name;

    @Convert(converter = UriPropertyConverter.class, columnType = String.class)
    private Uri imageUri;

    @Convert(converter = JodaTimePropertyConverter.class, columnType = long.class)
    @NotNull
    private DateTime creationDate;

    @Convert(converter = AmountUnitTypePropertyConverter.class, columnType = int.class)
    @NotNull
    private AmountUnitType amountType;

    @Convert(converter = BigDecimalPropertyConverter.class, columnType = String.class)
    @NotNull
    private BigDecimal energyDensityAmount;

    @ToMany(joinProperties = {
        @JoinProperty(name = "id", referencedName = "ingredientTypeId")
    })
    private List<Ingredient> childIngredients;

    @ToMany(joinProperties = {
        @JoinProperty(name = "id", referencedName = "ingredientTypeId")
    })
    private List<JointIngredientTag> tags;

    public IngredientTemplate(Long id) {
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

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    @NotNull
    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(@NotNull DateTime creationDate) {
        this.creationDate = creationDate;
    }

    @NotNull
    public AmountUnitType getAmountType() {
        return amountType;
    }

    public void setAmountType(@NotNull AmountUnitType amountType) {
        this.amountType = amountType;
    }

    @NotNull
    public BigDecimal getEnergyDensityAmount() {
        return energyDensityAmount;
    }

    public void setEnergyDensityAmount(@NotNull BigDecimal energyDensityAmount) {
        this.energyDensityAmount = energyDensityAmount;
    }

}
