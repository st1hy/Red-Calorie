package com.github.st1hy.countthemcalories.activities.tags.model.commands;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.tags.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;

public class TagsDatabaseCommands {
    private final RxTagsDatabaseModel databaseModel;
    private final AtomicReference<CommandResponse> responseAtomicReference = new AtomicReference<>();

    @Inject
    public TagsDatabaseCommands(@NonNull RxTagsDatabaseModel databaseModel) {
        this.databaseModel = databaseModel;
    }

    @NonNull
    public Observable<CommandResponse<Cursor, Cursor>> insert(@NonNull final Tag tag) {
        return insert(tag, Collections.<JointIngredientTag>emptyList());
    }

    @NonNull
    Observable<CommandResponse<Cursor, Cursor>> insert(@NonNull final Tag tag,
                                                       @NonNull final List<JointIngredientTag> jTags) {
        return new InsertCommand(databaseModel, commands, tag, jTags)
                .executeInTx()
                .compose(applyValidityCheck());
    }

    @NonNull
    public Observable<CommandResponse<Cursor, Cursor>> delete(@NonNull Tag tag) {
        return new DeleteCommand(databaseModel, this, tag)
                .executeInTx()
                .compose(applyValidityCheck());
    }

    @NonNull
    private Observable.Transformer<CommandResponse<Cursor, Cursor>, CommandResponse<Cursor, Cursor>> applyValidityCheck() {
        return new Observable.Transformer<CommandResponse<Cursor, Cursor>, CommandResponse<Cursor, Cursor>>() {
            @Override
            public Observable<CommandResponse<Cursor, Cursor>> call(Observable<CommandResponse<Cursor, Cursor>> observable) {
                return observable.doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        CommandResponse commandResponse = responseAtomicReference.getAndSet(null);
                        if (commandResponse != null) {
                            commandResponse.invalidate();
                        }
                    }
                }).doOnNext(new Action1<CommandResponse<Cursor, Cursor>>() {
                    @Override
                    public void call(CommandResponse<Cursor, Cursor> newResponse) {
                        CommandResponse commandResponse = responseAtomicReference.getAndSet(newResponse);
                        if (commandResponse != null) {
                            commandResponse.invalidate();
                        }
                    }
                });
            }
        };
    }

}
