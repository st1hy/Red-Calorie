package com.github.st1hy.countthemcalories.core.inject;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.github.st1hy.countthemcalories.inject.quantifier.context.ActivityContext;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class InputMethodManagerModule {

    @Provides
    public static InputMethodManager inputMethodManager(@ActivityContext Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }
}
