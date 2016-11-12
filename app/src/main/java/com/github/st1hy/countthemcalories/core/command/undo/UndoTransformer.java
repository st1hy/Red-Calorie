package com.github.st1hy.countthemcalories.core.command.undo;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.core.rx.Filters;
import com.github.st1hy.countthemcalories.core.rx.Functions;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class UndoTransformer<Response, UndoResponse> implements Observable.Transformer<Boolean, UndoResponse> {
    @NonNull
    final CommandResponse<Response, UndoResponse> response;
    @NonNull
    final Func1<Boolean, Observable<UndoAction>> selectUndoCall;

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
                .filter(Filters.equalTo(UndoAction.UNDO))
                .flatMap(onActionUndo())
                .map(Functions.<UndoResponse>intoResponse())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    private Func1<UndoAction, Observable<CommandResponse<UndoResponse, Response>>> onActionUndo() {
        return new Func1<UndoAction, Observable<CommandResponse<UndoResponse, Response>>>() {
            @Override
            public Observable<CommandResponse<UndoResponse, Response>> call(UndoAction action) {
                return response.undo();
            }
        };
    }
}
