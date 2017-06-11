package com.github.st1hy.countthemcalories.core.command;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.command.undo.UndoAvailability;

import rx.Observable;

public abstract class AbstractCommandResponse<Response, UndoResponse> implements CommandResponse<Response, UndoResponse> {
    private final Response response;
    private final UndoAvailability undoAvailability;
    private Observable<Boolean> undoAvailabilityObserver;

    public AbstractCommandResponse(Response response, boolean isUndoAvailable) {
        this.response = response;
        undoAvailability = new UndoAvailability(isUndoAvailable);
    }

    @NonNull
    @Override
    public Response getResponse() {
        return response;
    }

    @Override
    public boolean isUndoAvailable() {
        return undoAvailability.isUndoAvailable();
    }

    @NonNull
    @Override
    public Observable<Boolean> undoAvailability() {
        if (undoAvailabilityObserver == null) {
            undoAvailabilityObserver = Observable.unsafeCreate(undoAvailability).replay().autoConnect();
        }
        return undoAvailabilityObserver;
    }

    @NonNull
    @Override
    public Observable<CommandResponse<UndoResponse, Response>> undo() {
        if (isUndoAvailable()) {
            return reverseIfAvailable();
        } else
            return getError();
    }

    @Override
    public void invalidate() {
        undoAvailability.invalidate();
    }

    private Observable<CommandResponse<UndoResponse, Response>> reverseIfAvailable() {
        return undoAvailability()
                .firstOrDefault(false)
                .filter(isAvailable -> isAvailable)
                .doOnNext(aBoolean -> invalidate())
                .switchMap(isAvailable1 -> reverseCommand())
                .mergeWith(undoAvailability()
                        .firstOrDefault(false)
                        .filter(isAvailable -> !isAvailable)
                        .switchMap(aBoolean -> getError())
                );
    }

    @NonNull
    private Observable<CommandResponse<UndoResponse, Response>> getError() {
        return Observable.error(new IllegalStateException("Cannot undo command"));
    }

    @NonNull
    protected abstract Observable<CommandResponse<UndoResponse, Response>> reverseCommand();

}
