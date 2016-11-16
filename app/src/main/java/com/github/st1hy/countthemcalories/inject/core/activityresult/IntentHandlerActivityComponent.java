package com.github.st1hy.countthemcalories.inject.core.activityresult;

import com.github.st1hy.countthemcalories.core.activityresult.IntentHandlerActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent
public interface IntentHandlerActivityComponent {

    void inject(IntentHandlerActivity activity);
}
