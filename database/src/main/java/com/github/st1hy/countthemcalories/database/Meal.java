package com.github.st1hy.countthemcalories.database;

import android.net.Uri;

import com.github.st1hy.countthemcalories.database.property.JodaTimePropertyConverter;
import com.github.st1hy.countthemcalories.database.property.UriPropertyConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.joda.time.DateTime;

import java.util.List;

@Entity(active = true, nameInDb = "MEALS")
public class Meal {

    @Id(autoincrement = true)
    @Index(unique = true)
    private Long id;

    @Index
    private String name;

    @Convert(converter = UriPropertyConverter.class, columnType = String.class)
    private Uri imageUri;

    @Convert(converter = JodaTimePropertyConverter.class, columnType = long.class)
    @NotNull
    private DateTime creationDate;

    @ToMany(joinProperties = {
        @JoinProperty(name = "id", referencedName = "partOfMealId")
    })
    private List<Ingredient> ingredients;

    public Meal(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
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

}
