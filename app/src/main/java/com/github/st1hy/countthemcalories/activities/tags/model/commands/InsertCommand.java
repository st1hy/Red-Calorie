package com.github.st1hy.countthemcalories.activities.tags.model.commands;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.tags.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.core.command.AbstractCommandResponse;
import com.github.st1hy.countthemcalories.core.command.Command;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;

import java.util.List;

import rx.Observable;

class InsertCommand implements Command<InsertResult, Cursor> {
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
        int position = getNewItemPosition(cursor, tag);
        return new InsertResult(cursor, position);
    }

    /**
     * @return position of new item or -1 if item could not be found inside the cursor
     */
    private int getNewItemPosition(@NonNull Cursor cursor, @NonNull Tag dao) {
        int idColumn = cursor.getColumnIndexOrThrow(TagDao.Properties.Id.columnName);
        long newDaoId = dao.getId();
        while (cursor.moveToNext()) {
            if (newDaoId == cursor.getLong(idColumn)) {
                return cursor.getPosition();
            }
        }
        return -1;
    }

    class InsertResponse extends AbstractCommandResponse<InsertResult, Cursor> {

        public InsertResponse(@NonNull InsertResult result) {
            super(result, true);
        }
        @NonNull
        @Override
        protected Observable<CommandResponse<Cursor, InsertResult>> reverseCommand() {
            return commands.delete(tag);
        }

    }
}
