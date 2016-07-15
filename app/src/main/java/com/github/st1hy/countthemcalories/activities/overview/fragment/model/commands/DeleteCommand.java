package com.github.st1hy.countthemcalories.activities.overview.fragment.model.commands;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.github.st1hy.countthemcalories.activities.overview.fragment.model.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.core.command.AbstractCommandResponse;
import com.github.st1hy.countthemcalories.core.command.Command;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.Meal;

import java.util.List;

import rx.Observable;

public class DeleteCommand implements Command<Void, Meal> {

    final RxMealsDatabaseModel databaseModel;
    final MealsDatabaseCommands commands;
    final Meal meal;

    public DeleteCommand(@NonNull RxMealsDatabaseModel databaseModel,
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
        return new DeleteResponse(whatsRemoved.first, whatsRemoved.second);
    }

    class DeleteResponse extends AbstractCommandResponse<Void, Meal> {
        final Meal removedMeal;
        final List<Ingredient> removedIngredients;

        public DeleteResponse(@NonNull Meal removedMeal,
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
