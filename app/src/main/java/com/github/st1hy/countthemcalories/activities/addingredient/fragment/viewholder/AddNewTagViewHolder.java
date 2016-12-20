package com.github.st1hy.countthemcalories.activities.addingredient.fragment.viewholder;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.rx.Transformers;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class AddNewTagViewHolder extends TagViewHolder {

    @BindView(R.id.add_ingredient_category_add)
    View addNewTag;
    private final CompositeSubscription subscriptions = new CompositeSubscription();


    public AddNewTagViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void onAttached(PublishSubject<Void> events) {
        subscriptions.add(
                RxView.clicks(addNewTag)
                        .compose(Transformers.channel(events))
                        .subscribe()
        );
    }

    public void onDetached() {
        subscriptions.clear();
    }
}
