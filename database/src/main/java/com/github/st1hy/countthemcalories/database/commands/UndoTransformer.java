package com.github.st1hy.countthemcalories.database.commands;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class UndoTransformer<Response, UndoResponse> implements
        Observable.Transformer<Boolean, UndoResponse> {
    @NonNull
    private final CommandResponse<Response, UndoResponse> response;
    @NonNull
    private final Func1<Boolean, Observable<UndoAction>> selectUndoCall;

    public UndoTransformer(@NonNull CommandResponse<Response, UndoResponse> response,
                           @NonNull Func1<Boolean, Observable<UndoAction>> selectUndoCall) {
        this.response = response;
        this.selectUndoCall = selectUndoCall;
    }

    @Override
    public Observable<UndoResponse> call(@NonNull Observable<Boolean> availability) {
        return availability
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(selectUndoCall)
                .flatMap(action -> response.undo())
                .map(CommandResponse::getResponse)
                .observeOn(AndroidSchedulers.mainThread());
    }

}
