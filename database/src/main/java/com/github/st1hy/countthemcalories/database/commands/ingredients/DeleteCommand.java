package com.github.st1hy.countthemcalories.database.commands.ingredients;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.rx.IngredientRemovalEffect;
import com.github.st1hy.countthemcalories.database.rx.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.database.commands.AbstractCommandResponse;
import com.github.st1hy.countthemcalories.database.commands.Command;
import com.github.st1hy.countthemcalories.database.commands.CommandResponse;
import com.github.st1hy.countthemcalories.database.commands.InsertResult;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import rx.Observable;

class DeleteCommand implements Command<Cursor, InsertResult> {

    private final RxIngredientsDatabaseModel databaseModel;
    private final IngredientsDatabaseCommands commands;
    private final IngredientTemplate ingredient;

    DeleteCommand(@NonNull RxIngredientsDatabaseModel databaseModel,
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
        IngredientRemovalEffect removalEffect = databaseModel.performRemoveRaw(ingredient);
        Cursor cursor = databaseModel.refresh().call();
        return new DeleteResponse(cursor, removalEffect);
    }

    private class DeleteResponse extends AbstractCommandResponse<Cursor, InsertResult> {
        IngredientRemovalEffect whatsRemoved;

        DeleteResponse(@NonNull Cursor cursor, @NonNull IngredientRemovalEffect whatsRemoved) {
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
