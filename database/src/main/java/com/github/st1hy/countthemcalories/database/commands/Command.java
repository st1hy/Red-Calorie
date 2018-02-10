package com.github.st1hy.countthemcalories.database.commands;

import java.util.concurrent.Callable;

import rx.Observable;

public interface Command<Response, UndoResponse> extends Callable<CommandResponse<Response, UndoResponse>> {

    Observable<CommandResponse<Response, UndoResponse>> executeInTx();
}
