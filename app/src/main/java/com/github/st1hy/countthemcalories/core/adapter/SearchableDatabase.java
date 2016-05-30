package com.github.st1hy.countthemcalories.core.adapter;

import android.database.Cursor;
import android.support.annotation.NonNull;

import rx.Observable;

public interface SearchableDatabase {

    @NonNull
    Observable<Cursor> getAllFiltered(@NonNull String partOfName);
}
