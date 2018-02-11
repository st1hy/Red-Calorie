package com.github.st1hy.countthemcalories.database.commands.meals;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.github.st1hy.countthemcalories.database.rx.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.database.commands.AbstractCommandResponse;
import com.github.st1hy.countthemcalories.database.commands.Command;
import com.github.st1hy.countthemcalories.database.commands.CommandResponse;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.Meal;

import java.util.List;

import rx.Observable;

import static com.google.common.base.Preconditions.checkNotNull;

class DeleteCommand implements Command<Void, Meal> {

    @NonNull
    private final RxMealsDatabaseModel databaseModel;
    @NonNull
    private final MealsDatabaseCommands commands;
    @NonNull
    private final Meal meal;

    DeleteCommand(@NonNull RxMealsDatabaseModel databaseModel,
                         @NonNull MealsDatabaseCommands commands,
                         @NonNull Meal meal) {
        this.databaseModel = databaseModel;
        this.commands = commands;
        this.meal = meal;
    }

    @Override
    public Observable<CommandResponse<Void, Meal>> executeInTx() {
        return databaseModel.fromDatabaseTask(this);
    }

    @Override
    public CommandResponse<Void, Meal> call() throws Exception {
        Pair<Meal, List<Ingredient>> whatsRemoved = databaseModel.performRemoveRaw(meal);
        return new DeleteResponse(checkNotNull(whatsRemoved.first), checkNotNull(whatsRemoved.second));
    }

    private class DeleteResponse extends AbstractCommandResponse<Void, Meal> {
        final Meal removedMeal;
        final List<Ingredient> removedIngredients;

        DeleteResponse(@NonNull Meal removedMeal,
                              @NonNull List<Ingredient> removedIngredients) {
            super(null, true);
            this.removedMeal = removedMeal;
            this.removedIngredients = removedIngredients;
        }

        @NonNull
        @Override
        protected Observable<CommandResponse<Meal, Void>> reverseCommand() {
            return commands.insert(removedMeal, removedIngredients);
        }
    }


}
