package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.BasicLifecycle;

public interface IngredientDetailPresenter extends BasicLifecycle {

    void onSaveState(@NonNull Bundle outState);
}
