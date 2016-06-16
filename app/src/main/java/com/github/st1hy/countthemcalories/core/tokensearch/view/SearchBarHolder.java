package com.github.st1hy.countthemcalories.core.tokensearch.view;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class SearchBarHolder {

    @BindView(R.id.ingredients_search_view)
    TokenSearchView searchView;
    @BindView(R.id.ingredients_search_expand)
    View expand;
    @BindView(R.id.ingredients_search_collapse)
    View collapse;

    public SearchBarHolder(@NonNull BaseActivity activity) {
        ButterKnife.bind(this, activity);
    }

    public void expand() {
        expand.setVisibility(View.GONE);
        collapse.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.VISIBLE);
    }

    public void collapse() {
        expand.setVisibility(View.VISIBLE);
        collapse.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);
    }

    @NonNull
    public Observable<Void> expandClicked() {
        return RxView.clicks(expand);
    }

    @NonNull
    public Observable<Void> collapseClicked() {
        return RxView.clicks(collapse);
    }
}
