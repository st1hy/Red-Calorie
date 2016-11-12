package com.github.st1hy.countthemcalories.activities.contribute.inject;


import com.github.st1hy.countthemcalories.activities.contribute.view.ContributeActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerModule;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = {ContributeActivityModule.class, DrawerModule.class})
public interface ContributeActivityComponent {

    void inject(ContributeActivity activity);

}
