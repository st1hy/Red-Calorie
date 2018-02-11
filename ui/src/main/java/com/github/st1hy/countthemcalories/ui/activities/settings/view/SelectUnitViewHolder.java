package com.github.st1hy.countthemcalories.ui.activities.settings.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class SelectUnitViewHolder {

    @BindView(R.id.settings_unit_item_root)
    View root;
    @BindView(R.id.settings_unit_item_title)
    TextView title;
    @BindView(R.id.settings_unit_item_current_unit)
    TextView unit;

    public SelectUnitViewHolder(@NonNull View root) {
        ButterKnife.bind(this, root);
    }

    public void setTitle(@StringRes int titleRes) {
        title.setText(titleRes);
    }

    public void setUnit(@NonNull String unit) {
        this.unit.setText(unit);
    }

    @NonNull
    public Observable<Void> clickObservable() {
        return RxView.clicks(root);
    }
}
