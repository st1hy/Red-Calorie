package com.github.st1hy.countthemcalories.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;

@Entity(nameInDb = "i18n")
public class I18n {

    @Id
    @Index
    @Property(nameInDb = "_id")
    private long id;

    @Index(unique = true)
    @NotNull
    @Property(nameInDb = "en")
    private String english;

    @NotNull
    @Property(nameInDb = "pl")
    private String polish;

    @Generated(hash = 366971769)
    public I18n(long id, @NotNull String english, @NotNull String polish) {
        this.id = id;
        this.english = english;
        this.polish = polish;
    }

    @Generated(hash = 1094573538)
    public I18n() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnglish() {
        return this.english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getPolish() {
        return this.polish;
    }

    public void setPolish(String polish) {
        this.polish = polish;
    }

    public void setId(long id) {
        this.id = id;
    }
}
