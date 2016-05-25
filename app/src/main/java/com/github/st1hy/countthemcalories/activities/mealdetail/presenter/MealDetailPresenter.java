package com.github.st1hy.countthemcalories.activities.mealdetail.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

public interface MealDetailPresenter {
    void onStart();

    void onStop();

    void onSaveState(@NonNull Bundle outState);
}