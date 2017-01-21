package com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;

import rx.Observable;
import rx.subjects.PublishSubject;

public abstract class TagViewHolder extends RecyclerView.ViewHolder {


    public TagViewHolder(@NonNull View itemView) {
        super(itemView);
    }


    public abstract void onAttached(@NonNull Observable<RecyclerEvent> events,
                                    @NonNull PublishSubject<TagViewHolder> stateChanges);

    public abstract void onDetached();
}
