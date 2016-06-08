package com.github.st1hy.countthemcalories.activities.ingredients.model.commands;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.ingredients.model.RemovalEffect;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.core.command.AbstractCommandResponse;
import com.github.st1hy.countthemcalories.core.command.Command;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.core.command.InsertResult;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import rx.Observable;

public class DeleteCommand implements Command<Cursor, InsertResult> {

    final RxIngredientsDatabaseModel databaseModel;
    final IngredientsDatabaseCommands commands;
    final IngredientTemplate ingredient;

    public DeleteCommand(@NonNull RxIngredientsDatabaseModel databaseModel,
                         @NonNull IngredientsDatabaseCommands commands,
                         @NonNull IngredientTemplate ingredient) {
        this.databaseModel = databaseModel;
        this.commands = commands;
        this.ingredient = ingredient;
    }

    @Override
    public Observable<CommandResponse<Cursor, InsertResult>> executeInTx() {
        return databaseModel.fromDatabaseTask(this);
    }

    @Override
    public CommandResponse<Cursor, InsertResult> call() throws Exception {
        RemovalEffect removalEffect = databaseModel.performRemoveRaw(ingredient);
        Cursor cursor = databaseModel.refresh().call();
        return new DeleteResponse(cursor, removalEffect);
    }

    class DeleteResponse extends AbstractCommandResponse<Cursor, InsertResult> {
        RemovalEffect whatsRemoved;

        public DeleteResponse(@NonNull Cursor cursor, @NonNull RemovalEffect whatsRemoved) {
            super(cursor, true);
            this.whatsRemoved = whatsRemoved;
        }

        @NonNull
        @Override
        protected Observable<CommandResponse<InsertResult, Cursor>> reverseCommand() {
            return commands.insert(whatsRemoved);
        }
    }


}
