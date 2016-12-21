package com.github.st1hy.countthemcalories.activities.tags.fragment.model.commands;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.tags.fragment.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.core.command.InsertResult;
import com.github.st1hy.countthemcalories.core.command.undo.UndoInvalidator;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import rx.Observable;

public class TagsDatabaseCommands {
    private final RxTagsDatabaseModel databaseModel;

    private final AtomicReference<CommandResponse> responseRef = new AtomicReference<>();
    private final UndoInvalidator<InsertResult, Cursor> insertInvalidator = new UndoInvalidator<>(responseRef);
    private final UndoInvalidator<Cursor, InsertResult> deleteInvalidator = new UndoInvalidator<>(responseRef);

    @Inject
    public TagsDatabaseCommands(@NonNull RxTagsDatabaseModel databaseModel) {
        this.databaseModel = databaseModel;
    }

    @NonNull
    public Observable<CommandResponse<InsertResult, Cursor>> insert(@NonNull final Tag tag) {
        return insert(tag, Collections.emptyList());
    }

    @NonNull
    Observable<CommandResponse<InsertResult, Cursor>> insert(@NonNull final Tag tag,
                                                             @NonNull final List<JointIngredientTag> jTags) {
        return new InsertCommand(databaseModel, this, tag, jTags)
                .executeInTx()
                .compose(insertInvalidator);
    }

    @NonNull
    public Observable<CommandResponse<Cursor, InsertResult>> delete(@NonNull Tag tag) {
        return new DeleteCommand(databaseModel, this, tag)
                .executeInTx()
                .compose(deleteInvalidator);
    }

}
