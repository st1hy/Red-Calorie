package com.github.st1hy.countthemcalories.activities.addmeal.inject;

import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

public interface AddMealActivityComponentFactory {

    AddMealActivityComponent newAddMealActivityComponent(ActivityModule activityModule);
}
