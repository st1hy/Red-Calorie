package com.github.st1hy.countthemcalories.activities.settings.view.holder;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectUnitViewHolder {
    private final View root;

    @BindView(R.id.settings_unit_item_title)
    TextView title;
    @BindView(R.id.settings_unit_item_current_unit)
    TextView unit;

    public SelectUnitViewHolder(@NonNull View root) {
        this.root = root;
        ButterKnife.bind(this, root);
    }

    @NonNull
    public View getRoot() {
        return root;
    }

    public void setTitle(@StringRes int titleRes) {
        title.setText(titleRes);
    }

    public void setUnit(@NonNull String unit) {
        this.unit.setText(unit);
    }
}
