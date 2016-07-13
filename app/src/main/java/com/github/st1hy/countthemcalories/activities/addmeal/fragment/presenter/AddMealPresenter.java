package com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.withpicture.presenter.WithPicturePresenter;

public interface AddMealPresenter extends WithPicturePresenter {

    void onSaveState(@NonNull Bundle outState);

}
