package com.github.st1hy.countthemcalories.core.command.undo;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.command.CommandResponse;

import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;

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
                .doOnSubscribe(() -> {
                    CommandResponse commandResponse1 = lastResponse.getAndSet(null);
                    if (commandResponse1 != null) {
                        commandResponse1.invalidate();
                    }
                })
                .doOnNext(newResponse -> {
                    CommandResponse commandResponse = lastResponse.getAndSet(newResponse);
                    if (commandResponse != null) {
                        commandResponse.invalidate();
                    }
                });
    }

}
