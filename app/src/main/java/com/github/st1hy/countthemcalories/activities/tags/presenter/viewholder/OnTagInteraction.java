package com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.database.Tag;

import rx.Observable;

public interface OnTagInteraction {

    void onTagClicked(int position, @NonNull Tag tag);

    void onTagLongClicked(int position, @NonNull Tag tag);

    @NonNull
    Observable<RecyclerEvent> getEvents();
}
