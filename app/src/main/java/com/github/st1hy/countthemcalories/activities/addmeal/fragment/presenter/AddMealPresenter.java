package com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.picture.PicturePresenter;

public interface AddMealPresenter extends PicturePresenter {

    void onSaveState(@NonNull Bundle outState);

}
