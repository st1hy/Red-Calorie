package com.github.st1hy.countthemcalories.database.commands.meals;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.rx.RxMealsDatabaseModel;
import com.github.st1hy.countthemcalories.database.commands.AbstractCommandResponse;
import com.github.st1hy.countthemcalories.database.commands.Command;
import com.github.st1hy.countthemcalories.database.commands.CommandResponse;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.Meal;

import java.util.List;

import rx.Observable;

class InsertCommand implements Command<Meal, Void> {
    @NonNull
    private final RxMealsDatabaseModel databaseModel;
    @NonNull
    private final MealsDatabaseCommands commands;
    @NonNull
    private final Meal meal;
    @NonNull
    private final List<Ingredient> ingredients;

    InsertCommand(@NonNull RxMealsDatabaseModel databaseModel,
                         @NonNull MealsDatabaseCommands commands,
                         @NonNull Meal meal,
                         @NonNull List<Ingredient> ingredients) {
        this.databaseModel = databaseModel;
        this.commands = commands;
        this.meal = meal;
        this.ingredients = ingredients;
    }

    @Override
    public Observable<CommandResponse<Meal, Void>> executeInTx() {
        return databaseModel.fromDatabaseTask(this);
    }

    @Override
    public CommandResponse<Meal, Void> call() throws Exception {
        Meal insertedMeal = databaseModel.performInsertOrUpdate(meal, ingredients);
        return new InsertResponse(insertedMeal);
    }

    private class InsertResponse extends AbstractCommandResponse<Meal, Void> {

        public InsertResponse(@NonNull Meal result) {
            super(result, false);
        }

        @NonNull
        @Override
        protected Observable<CommandResponse<Void, Meal>> reverseCommand() {
            return commands.delete(getResponse());
        }
    }
}
