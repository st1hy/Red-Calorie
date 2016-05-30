package com.github.st1hy.countthemcalories.core.command;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public abstract class AbstractCommandResponse<Response, UndoResponse> implements CommandResponse<Response, UndoResponse> {
    private final Response response;
    private final UndoAvailability undoAvailability;
    private Observable<Boolean> undoAvailabilityObserver;

    public AbstractCommandResponse(@NonNull Response response, boolean isUndoAvailable) {
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
            undoAvailabilityObserver = Observable.create(undoAvailability).replay().autoConnect();
        }
        return undoAvailabilityObserver;
    }

    @NonNull
    @Override
    public Command<UndoResponse, Response> undo() {
        return new Command<UndoResponse, Response>() {
            @Override
            public Observable<CommandResponse<UndoResponse, Response>> execute() {
                return reverseIfAvailable();
            }
        };
    }

    private Observable<CommandResponse<UndoResponse, Response>> reverseIfAvailable() {
        return undoAvailability().firstOrDefault(false)
                .filter(new Func1<Boolean, Boolean>() {
                    @Override
                    public Boolean call(Boolean isAvailable) {
                        return isAvailable;
                    }
                })
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        invalidateUndo();
                    }
                })
                .switchMap(new Func1<Boolean, Observable<CommandResponse<UndoResponse, Response>>>() {
                    @Override
                    public Observable<CommandResponse<UndoResponse, Response>> call(Boolean isAvailable) {
                        return reverseCommand().execute();
                    }
                }).mergeWith(undoAvailability()
                        .firstOrDefault(false)
                        .filter(new Func1<Boolean, Boolean>() {
                            @Override
                            public Boolean call(Boolean isAvailable) {
                                return !isAvailable;
                            }
                        }).switchMap(new Func1<Boolean, Observable<? extends CommandResponse<UndoResponse, Response>>>() {
                            @Override
                            public Observable<? extends CommandResponse<UndoResponse, Response>> call(Boolean aBoolean) {
                                return Observable.error(new IllegalStateException("Cannot undo command"));
                            }
                        })
                );
    }

    @NonNull
    private Action0 invalidateUndo() {
        return new Action0() {
            @Override
            public void call() {
                undoAvailability.invalidate();
            }
        };
    }

    @NonNull
    protected abstract Command<UndoResponse, Response> reverseCommand();

}
