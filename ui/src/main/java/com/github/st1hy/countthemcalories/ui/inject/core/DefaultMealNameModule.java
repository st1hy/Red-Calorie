package com.github.st1hy.countthemcalories.ui.inject.core;

import com.github.st1hy.countthemcalories.ui.core.meals.DefaultMealName;
import com.github.st1hy.countthemcalories.ui.core.meals.DefaultMealNameImpl;
import com.github.st1hy.countthemcalories.ui.core.meals.DefaultNameEn;
import com.github.st1hy.countthemcalories.ui.core.meals.DefaultNamePl;
import com.github.st1hy.countthemcalories.ui.core.meals.DefaultNameSelector;

import java.util.Locale;
import java.util.Map;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module
public abstract class DefaultMealNameModule {

    @Provides
    static DefaultNameSelector defaultMealNameEngine(
            Map<String, DefaultNameSelector> supported,
            @Named("langCode") String langCode) {
        DefaultNameSelector defaultMealName = supported.get(langCode);
        if (defaultMealName == null) {
            defaultMealName = supported.get("en");
        }
        return defaultMealName;
    }

    @Provides
    @Named("langCode")
    static String langCode() {
        return Locale.getDefault().getLanguage();
    }

    @Binds
    abstract DefaultMealName defaultMealName(DefaultMealNameImpl defaultMealName);

    @Binds
    @IntoMap
    @StringKey("pl")
    abstract DefaultNameSelector defaultMealNameEnginePL(DefaultNamePl engine);

    @Binds
    @IntoMap
    @StringKey("en")
    abstract DefaultNameSelector defaultMealNameEngineEN(DefaultNameEn engine);
}
