package com.github.st1hy.countthemcalories.core.rx.activityresult.inject;

import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.rx.activityresult.ShadowActivity;

import dagger.Component;

@Component(dependencies = ApplicationComponent.class)
public interface ShadowActivityComponent {

    void inject(ShadowActivity activity);
}
