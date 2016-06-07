package com.github.st1hy.countthemcalories.activities.ingredients.model.commands;

import android.database.Cursor;

import com.github.st1hy.countthemcalories.core.command.Command;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;

import rx.Observable;

public class DeleteCommand implements Command<Cursor, Cursor> {


    @Override
    public Observable<CommandResponse<Cursor, Cursor>> executeInTx() {
        return null;
    }

    @Override
    public CommandResponse<Cursor, Cursor> call() throws Exception {
        return null;
    }


}
