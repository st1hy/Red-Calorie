package com.github.st1hy.countthemcalories.activities.addingredient.view.holder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.Bind;
import rx.Observable;

public class ItemTagViewHolder extends TagViewHolder {

    @Bind(R.id.add_ingredient_category_name)
    TextView categoryName;
    @Bind(R.id.add_ingredient_category_remove)
    Button removeTag;
    private final Observable<Void> observable;

    public ItemTagViewHolder(@NonNull View itemView) {
        super(itemView);
        observable = RxView.clicks(removeTag);
    }

    public void setCategoryName(@NonNull String name) {
        categoryName.setText(name);
    }

    @NonNull
    public Observable<Void> observableRemoval() {
        return observable;
    }

}
