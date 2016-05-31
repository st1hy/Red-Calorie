package com.github.st1hy.countthemcalories.activities.tags.model.commands;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.tags.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.core.command.AbstractCommandResponse;
import com.github.st1hy.countthemcalories.core.command.Command;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;

import java.util.List;

import rx.Observable;

class InsertCommand implements Command<Cursor, Cursor> {
    final RxTagsDatabaseModel databaseModel;
    final TagsDatabaseCommands commands;
    final Tag tag;
    final List<JointIngredientTag> jTags;

    public InsertCommand(@NonNull RxTagsDatabaseModel databaseModel,
                         @NonNull TagsDatabaseCommands commands,
                         @NonNull Tag tag,
                         @NonNull List<JointIngredientTag> jTags) {
        this.databaseModel = databaseModel;
        this.commands = commands;
        this.tag = tag;
        this.jTags = jTags;
    }

    @Override
    public Observable<CommandResponse<Cursor, Cursor>> executeInTx() {
        return databaseModel.fromDatabaseTask(this);
    }

    @Override
    public CommandResponse<Cursor, Cursor> call() throws Exception {
        databaseModel.performInsert(tag);
        if (!jTags.isEmpty()) {
            DaoSession session = databaseModel.session();
            session.getJointIngredientTagDao().insertInTx(jTags);
        }
        Cursor cursor = databaseModel.refresh().call();
        return new InsertResponse(cursor);
    }

    class InsertResponse extends AbstractCommandResponse<Cursor, Cursor> {

        public InsertResponse(@NonNull Cursor cursor) {
            super(cursor, true);
        }

        @NonNull
        @Override
        protected Observable<CommandResponse<Cursor, Cursor>> reverseCommand() {
            return commands.delete(tag);
        }
    }
}
