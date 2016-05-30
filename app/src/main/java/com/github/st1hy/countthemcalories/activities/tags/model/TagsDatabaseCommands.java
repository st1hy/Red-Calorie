package com.github.st1hy.countthemcalories.activities.tags.model;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.command.AbstractCommandResponse;
import com.github.st1hy.countthemcalories.core.command.Command;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.database.Tag;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Func1;

public class TagsDatabaseCommands {
    private final RxTagsDatabaseModel databaseModel;

    @Inject
    public TagsDatabaseCommands(@NonNull RxTagsDatabaseModel databaseModel) {
        this.databaseModel = databaseModel;
    }

    @NonNull
    public Command<Cursor, Cursor> insert(@NonNull final Tag tag) {
        return new InsertCommand(databaseModel, tag);
    }

    public Command<Cursor, Cursor> delete(@NonNull Tag tag) {
        return new DeleteCommand(databaseModel, tag);
    }

    private static class InsertCommand extends TagCommand {

        public InsertCommand(@NonNull RxTagsDatabaseModel model, @NonNull Tag tag) {
            super(model, tag);
        }

        @Override
        public Observable<CommandResponse<Cursor, Cursor>> execute() {
            return model.addNewAndRefresh(tag).map(intoResponse());
        }

        @NonNull
        private Func1<Cursor, CommandResponse<Cursor, Cursor>> intoResponse() {
            return new Func1<Cursor, CommandResponse<Cursor, Cursor>>() {
                @Override
                public CommandResponse<Cursor, Cursor> call(final Cursor cursor) {
                    return new InsertResponse(cursor, model, tag);
                }
            };
        }

        static class InsertResponse extends TagResponse {

            public InsertResponse(@NonNull Cursor cursor, @NonNull RxTagsDatabaseModel model, @NonNull Tag tag) {
                super(cursor, model, tag);
            }

            @NonNull
            @Override
            protected Command<Cursor, Cursor> reverseCommand() {
                return new DeleteCommand(model, tag);
            }
        }
    }

    private static class DeleteCommand extends TagCommand {

        public DeleteCommand(@NonNull RxTagsDatabaseModel model, @NonNull Tag tag) {
            super(model, tag);
        }

        @Override
        public Observable<CommandResponse<Cursor, Cursor>> execute() {
            return model.removeAndRefresh(tag).map(intoDeleteResponse());
        }

        @NonNull
        private Func1<Cursor, CommandResponse<Cursor, Cursor>> intoDeleteResponse() {
            return new Func1<Cursor, CommandResponse<Cursor, Cursor>>() {
                @Override
                public CommandResponse<Cursor, Cursor> call(Cursor cursor) {
                    return new DeleteResponse(cursor, model, tag);
                }
            };
        }

        static class DeleteResponse extends TagResponse {

            public DeleteResponse(@NonNull Cursor cursor, @NonNull RxTagsDatabaseModel model, @NonNull Tag deletedTag) {
                super(cursor, model, deletedTag);
            }

            @NonNull
            @Override
            protected Command<Cursor, Cursor> reverseCommand() {
                return new InsertCommand(model, tag);
            }
        }
    }

    private static abstract class TagCommand implements Command<Cursor, Cursor> {
        protected final RxTagsDatabaseModel model;
        protected final Tag tag;

        public TagCommand(@NonNull RxTagsDatabaseModel model, @NonNull Tag tag) {
            this.model = model;
            this.tag = tag;
        }
    }

    private static abstract class TagResponse extends AbstractCommandResponse<Cursor, Cursor> {
        protected final RxTagsDatabaseModel model;
        protected final Tag tag;

        public TagResponse(@NonNull Cursor cursor, @NonNull RxTagsDatabaseModel model, @NonNull Tag tag) {
            super(cursor, true);
            this.model = model;
            this.tag = tag;
        }
    }

}
