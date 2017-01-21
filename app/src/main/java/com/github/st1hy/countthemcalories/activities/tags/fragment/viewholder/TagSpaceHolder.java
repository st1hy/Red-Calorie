package com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;

import rx.Observable;
import rx.subjects.PublishSubject;

public class TagSpaceHolder extends TagViewHolder {

    public TagSpaceHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void onAttached(@NonNull Observable<RecyclerEvent> events,
                           @NonNull PublishSubject<TagViewHolder> stateChanges) {

    }

    @Override
    public void onDetached() {

    }
}
