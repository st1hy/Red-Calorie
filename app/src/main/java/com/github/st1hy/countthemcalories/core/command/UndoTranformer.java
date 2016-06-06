package com.github.st1hy.countthemcalories.core.command;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.rx.Functions;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class UndoTranformer<Response, UndoResponse> implements Observable.Transformer<Boolean, UndoResponse> {
    @NonNull
    final CommandResponse<Response, UndoResponse> response;
    @NonNull
    final Func1<Boolean, Observable<Void>> selectUndoCall;

    public UndoTranformer(@NonNull CommandResponse<Response, UndoResponse> response,
                          @NonNull Func1<Boolean, Observable<Void>> selectUndoCall) {
        this.response = response;
        this.selectUndoCall = selectUndoCall;
    }

    @Override
    public Observable<UndoResponse> call(@NonNull Observable<Boolean> availability) {
        return availability
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(selectUndoCall)
                .flatMap(onActionUndo())
                .map(Functions.<UndoResponse>intoResponse())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    private Func1<Void, Observable<CommandResponse<UndoResponse, Response>>> onActionUndo() {
        return new Func1<Void, Observable<CommandResponse<UndoResponse, Response>>>() {
            @Override
            public Observable<CommandResponse<UndoResponse, Response>> call(Void aVoid) {
                return response.undo();
            }
        };
    }
}
