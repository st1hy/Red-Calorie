package com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.database.Tag;

import rx.Observable;

public interface OnTagInteraction {

    void onTagClicked(int position, @NonNull Tag tag);

    void onDeleteClicked(int position, @NonNull Tag tag);

    @NonNull
    Observable<RecyclerEvent> getEvents();

    void onEditClicked(int position, @NonNull Tag tag);
}
