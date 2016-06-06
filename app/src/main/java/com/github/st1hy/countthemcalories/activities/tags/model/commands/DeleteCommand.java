package com.github.st1hy.countthemcalories.activities.tags.model.commands;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.github.st1hy.countthemcalories.activities.tags.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.core.command.AbstractCommandResponse;
import com.github.st1hy.countthemcalories.core.command.Command;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;

import java.util.List;

import rx.Observable;

class DeleteCommand implements Command<Cursor, InsertResult> {
    final RxTagsDatabaseModel databaseModel;
    final TagsDatabaseCommands commands;
    final Tag tag;

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
        Tag dao = databaseModel.performGetById(tag.getId());
        Pair<Tag, List<JointIngredientTag>> tagListPair = databaseModel.rawRemove(dao);
        Cursor cursor = databaseModel.refresh().call();
        return new DeleteResponse(cursor, tagListPair.first, tagListPair.second);
    }

    class DeleteResponse extends AbstractCommandResponse<Cursor, InsertResult> {
        final Tag removedTag;
        final List<JointIngredientTag> removedJTags;

        public DeleteResponse(@NonNull Cursor cursor,
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
