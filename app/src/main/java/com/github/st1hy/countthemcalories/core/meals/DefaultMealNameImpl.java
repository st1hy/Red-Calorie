package com.github.st1hy.countthemcalories.core.meals;

import android.content.res.Resources;
import android.support.annotation.NonNull;

import org.joda.time.DateTime;

import javax.inject.Inject;

public class DefaultMealNameImpl implements DefaultMealName {

    @NonNull
    private final Resources resources;
    @NonNull
    private final DefaultNameSelector selector;


    @Inject
    public DefaultMealNameImpl(@NonNull Resources resources,
                               @NonNull DefaultNameSelector selector) {
        this.resources = resources;
        this.selector = selector;
    }

    @NonNull
    @Override
    public String getBestGuessMealNameAt(@NonNull DateTime time) {
        return resources.getString(selector.matchDate(time));
    }


}
