package com.github.st1hy.countthemcalories.activities.tags.fragment.model.commands;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.github.st1hy.countthemcalories.activities.tags.fragment.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.core.command.AbstractCommandResponse;
import com.github.st1hy.countthemcalories.core.command.Command;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.core.command.InsertResult;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;

import java.util.List;

import rx.Observable;

class DeleteCommand implements Command<Cursor, InsertResult> {
    @NonNull
    private final RxTagsDatabaseModel databaseModel;
    @NonNull
    private final TagsDatabaseCommands commands;
    @NonNull
    private final Tag tag;

    public DeleteCommand(@NonNull RxTagsDatabaseModel databaseModel,
                         @NonNull TagsDatabaseCommands commands,
                         @NonNull Tag tag) {
        this.databaseModel = databaseModel;
        this.commands = commands;
        this.tag = tag;
    }

    @Override
    public Observable<CommandResponse<Cursor, InsertResult>> executeInTx() {
        return databaseModel.fromDatabaseTask(this);
    }

    @Override
    public CommandResponse<Cursor, InsertResult> call() throws Exception {
        Pair<Tag, List<JointIngredientTag>> tagListPair = databaseModel.rawRemove(tag);
        Cursor cursor = databaseModel.refresh().call();
        return new DeleteResponse(cursor, tagListPair.first, tagListPair.second);
    }

    private class DeleteResponse extends AbstractCommandResponse<Cursor, InsertResult> {
        final Tag removedTag;
        final List<JointIngredientTag> removedJTags;

        DeleteResponse(@NonNull Cursor cursor,
                       @NonNull Tag removedTag,
                       @NonNull List<JointIngredientTag> removedJTags) {
            super(cursor, true);
            this.removedTag = removedTag;
            this.removedJTags = removedJTags;
        }

        @NonNull
        @Override
        protected Observable<CommandResponse<InsertResult, Cursor>> reverseCommand() {
            return commands.insert(tag, removedJTags);
        }
    }
}
