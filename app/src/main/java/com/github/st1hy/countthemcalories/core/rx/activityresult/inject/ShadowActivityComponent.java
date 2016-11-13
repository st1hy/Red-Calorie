package com.github.st1hy.countthemcalories.core.rx.activityresult.inject;

import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.github.st1hy.countthemcalories.core.rx.activityresult.IntentHandlerActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class)
public interface ShadowActivityComponent {

    void inject(IntentHandlerActivity activity);
}
