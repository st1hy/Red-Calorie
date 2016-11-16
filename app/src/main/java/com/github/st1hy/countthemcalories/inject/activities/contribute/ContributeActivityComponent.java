package com.github.st1hy.countthemcalories.inject.activities.contribute;


import com.github.st1hy.countthemcalories.activities.contribute.ContributeActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = ContributeActivityModule.class)
public interface ContributeActivityComponent {

    void inject(ContributeActivity activity);

}
