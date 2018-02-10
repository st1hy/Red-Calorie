package com.github.st1hy.countthemcalories.database.commands.tags;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.rx.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.database.commands.AbstractCommandResponse;
import com.github.st1hy.countthemcalories.database.commands.Command;
import com.github.st1hy.countthemcalories.database.commands.CommandResponse;
import com.github.st1hy.countthemcalories.database.commands.InsertResult;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;

import java.util.List;

import rx.Observable;

class InsertCommand implements Command<InsertResult, Cursor> {
    @NonNull
    private final RxTagsDatabaseModel databaseModel;
    @NonNull
    private final TagsDatabaseCommands commands;
    @NonNull
    private final Tag tag;
    @NonNull
    private final List<JointIngredientTag> jTags;

    InsertCommand(@NonNull RxTagsDatabaseModel databaseModel,
                         @NonNull TagsDatabaseCommands commands,
                         @NonNull Tag tag,
                         @NonNull List<JointIngredientTag> jTags) {
        this.databaseModel = databaseModel;
        this.commands = commands;
        this.tag = tag;
        this.jTags = jTags;
    }

    @Override
    public Observable<CommandResponse<InsertResult, Cursor>> executeInTx() {
        return databaseModel.fromDatabaseTask(this);
    }

    @Override
    public CommandResponse<InsertResult, Cursor> call() throws Exception {
        Tag added = databaseModel.performInsert(tag);
        if (!jTags.isEmpty()) {
            DaoSession session = databaseModel.session();
            session.getJointIngredientTagDao().insertInTx(jTags);
            resetIngredients();
        }
        Cursor cursor = databaseModel.refresh().call();
        InsertResult result = getResult(cursor, added);
        return new InsertResponse(result);
    }

    private void resetIngredients() {
        for (JointIngredientTag jTag : jTags) {
            IngredientTemplate ingredientType = jTag.getIngredientType();
            ingredientType.resetTags();
            ingredientType.getTags();
        }
    }

    @NonNull
    private InsertResult getResult(@NonNull Cursor cursor, @NonNull Tag tag) {
        int position = databaseModel.findInCursor(cursor, tag);
        return new InsertResult(cursor, position);
    }


    private class InsertResponse extends AbstractCommandResponse<InsertResult, Cursor> {

        InsertResponse(@NonNull InsertResult result) {
            super(result, false);
        }

        @NonNull
        @Override
        protected Observable<CommandResponse<Cursor, InsertResult>> reverseCommand() {
            return commands.delete(tag);
        }

    }
}
