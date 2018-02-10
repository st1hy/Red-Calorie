package com.github.st1hy.countthemcalories.database.commands.ingredients;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.rx.IngredientRemovalEffect;
import com.github.st1hy.countthemcalories.database.rx.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.database.commands.CommandResponse;
import com.github.st1hy.countthemcalories.database.commands.InsertResult;
import com.github.st1hy.countthemcalories.database.commands.UndoInvalidator;
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
    public Observable<CommandResponse<InsertResult, Cursor>> insert(@NonNull IngredientRemovalEffect whatsRemoved) {
        return new InsertCommand(databaseModel, this, whatsRemoved)
                .executeInTx()
                .compose(insertInvalidator);
    }
}
