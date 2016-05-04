package com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.callbacks.OnItemInteraction;
import com.github.st1hy.countthemcalories.database.Tag;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class TagItemViewHolder extends TagViewHolder implements View.OnLongClickListener, View.OnClickListener {

    @Bind(R.id.tags_item_name)
    TextView name;
    @Bind(R.id.tag_button)
    View button;

    final OnItemInteraction<Tag> listener;
    private Tag tag;
    private Subscriber<Tag> subscriber;

    public TagItemViewHolder(@NonNull View itemView, @NonNull OnItemInteraction<Tag> listener) {
        super(itemView);
        this.listener = listener;
        ButterKnife.bind(this, itemView);
        button.setOnLongClickListener(this);
        button.setOnClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        if (tag != null) {
            listener.onItemLongClicked(tag);
            return true;
        } else return false;
    }

    @Override
    public void onClick(View v) {
        if (tag != null) {
            listener.onItemClicked(tag);
        }
    }

    public Subscription bind(Observable<Tag> observable) {
        if (subscriber != null) subscriber.unsubscribe();
        subscriber = new Subscriber<Tag>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Error on binding tag to view");
            }

            @Override
            public void onNext(Tag tag) {
                TagItemViewHolder.this.tag = tag;
                name.setText(tag.getName());
            }
        };
        return observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
