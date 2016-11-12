package com.github.st1hy.countthemcalories.core.command.undo;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.command.CommandResponse;

import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

public class UndoInvalidator<Re, Un> implements Observable.Transformer<CommandResponse<Re, Un>, CommandResponse<Re, Un>> {

    @NonNull
    private final AtomicReference<CommandResponse> lastResponse;

    public UndoInvalidator() {
        lastResponse = new AtomicReference<>();
    }

    public UndoInvalidator(@NonNull AtomicReference<CommandResponse> lastResponse) {
        this.lastResponse = lastResponse;
    }

    @Override
    public Observable<CommandResponse<Re, Un>> call(Observable<CommandResponse<Re, Un>> observable) {
        return observable
                .doOnSubscribe(invalidateLast())
                .doOnNext(invalidateLastAndSet());
    }

    @NonNull
    private Action1<CommandResponse<Re, Un>> invalidateLastAndSet() {
        return new Action1<CommandResponse<Re, Un>>() {
            @Override
            public void call(CommandResponse<Re, Un> newResponse) {
                CommandResponse commandResponse = lastResponse.getAndSet(newResponse);
                if (commandResponse != null) {
                    commandResponse.invalidate();
                }
            }
        };
    }

    @NonNull
    private Action0 invalidateLast() {
        return new Action0() {
            @Override
            public void call() {
                CommandResponse commandResponse = lastResponse.getAndSet(null);
                if (commandResponse != null) {
                    commandResponse.invalidate();
                }
            }
        };
    }
}
