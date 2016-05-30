package com.github.st1hy.countthemcalories.core.command;

import rx.Observable;

public interface Command<Response,UndoResponse> {

    Observable<CommandResponse<Response,UndoResponse>> execute();
}
