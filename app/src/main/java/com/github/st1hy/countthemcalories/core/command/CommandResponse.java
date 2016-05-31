package com.github.st1hy.countthemcalories.core.command;

import android.support.annotation.NonNull;

import rx.Observable;

public interface CommandResponse<Response, UndoResponse> {

    /**
     * @return response from the execution of the command
     */
    @NonNull
    Response getResponse();

    /**
     * @return true if undo action is possible
     */
    boolean isUndoAvailable();

    /**
     * Provides live updates when undo action availability changes from true to false.
     * It will replay last state once subscribed, may eventually call onNext(false) and than onCompleted()
     */
    @NonNull
    Observable<Boolean> undoAvailability();

    /**
     * Traces back this operation and reverts to previous state.
     *
     * @return command for undo operation, may immediately call onError if undo is not available
     * See {@link CommandResponse#isUndoAvailable()} to check if this will happen.
     */
    @NonNull
    Observable<CommandResponse<UndoResponse, Response>> undo();

    void invalidate();
}
