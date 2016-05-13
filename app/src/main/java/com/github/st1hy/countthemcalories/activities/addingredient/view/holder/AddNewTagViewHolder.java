package com.github.st1hy.countthemcalories.activities.addingredient.view.holder;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import rx.Observable;

public class AddNewTagViewHolder extends TagViewHolder {

    @BindView(R.id.add_ingredient_category_add)
    View addNewTag;
    private final Observable<Void> observable;

    public AddNewTagViewHolder(@NonNull View itemView) {
        super(itemView);
        observable = RxView.clicks(addNewTag);
    }

    @NonNull
    public Observable<Void> addNewObservable() {
        return observable;
    }
}
