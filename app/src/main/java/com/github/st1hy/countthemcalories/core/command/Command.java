package com.github.st1hy.countthemcalories.core.command;

import java.util.concurrent.Callable;

import rx.Observable;

public interface Command<Response,UndoResponse> extends Callable<CommandResponse<Response,UndoResponse>> {

    Observable<CommandResponse<Response,UndoResponse>> executeInTx();
}
