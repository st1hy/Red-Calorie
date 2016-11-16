package com.github.st1hy.countthemcalories.activities.ingredients.inject;

import com.github.st1hy.countthemcalories.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.inject.application.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.command.undo.UndoView;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = IngredientsActivityModule.class, dependencies = ApplicationComponent.class)
public interface IngredientsActivityComponent {

    void inject(IngredientsActivity activity);

    UndoView undoView();
}
