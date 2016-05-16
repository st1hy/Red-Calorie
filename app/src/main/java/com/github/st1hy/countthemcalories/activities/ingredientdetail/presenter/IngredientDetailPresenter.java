package com.github.st1hy.countthemcalories.activities.ingredientdetail.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

public interface IngredientDetailPresenter {

    void onStart();

    void onStop();

    void onSaveState(@NonNull Bundle outState);
}
