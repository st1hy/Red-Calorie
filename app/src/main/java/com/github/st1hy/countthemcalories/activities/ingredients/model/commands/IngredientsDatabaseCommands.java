package com.github.st1hy.countthemcalories.activities.ingredients.model.commands;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.ingredients.model.RemovalEffect;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.core.command.InsertResult;
import com.github.st1hy.countthemcalories.core.command.UndoInvalidator;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import rx.Observable;

public class IngredientsDatabaseCommands {
    private final RxIngredientsDatabaseModel databaseModel;

    private final AtomicReference<CommandResponse> responseRef = new AtomicReference<>();
    private final UndoInvalidator<InsertResult, Cursor> insertInvalidator = new UndoInvalidator<>(responseRef);
    private final UndoInvalidator<Cursor, InsertResult> deleteInvalidator = new UndoInvalidator<>(responseRef);

    @Inject
    public IngredientsDatabaseCommands(@NonNull RxIngredientsDatabaseModel databaseModel) {
        this.databaseModel = databaseModel;
    }

    @NonNull
    public Observable<CommandResponse<Cursor, InsertResult>> delete(@NonNull IngredientTemplate ingredient) {
        return new DeleteCommand(databaseModel, this, ingredient)
                .executeInTx()
                .compose(deleteInvalidator);
    }

    @NonNull
    public Observable<CommandResponse<InsertResult, Cursor>> insert(@NonNull RemovalEffect whatsRemoved) {
        return new InsertCommand(databaseModel, this, whatsRemoved)
                .executeInTx()
                .compose(insertInvalidator);
    }
}
