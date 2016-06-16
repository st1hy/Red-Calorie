package com.github.st1hy.countthemcalories.core.tokensearch.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.tokensearch.model.RxTokenSearchView;
import com.github.st1hy.countthemcalories.core.tokensearch.model.SearchBarModel;
import com.github.st1hy.countthemcalories.core.tokensearch.model.SearchResult;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class SearchBarHolder {

    final SearchBarModel model;

    @BindView(R.id.token_search_root)
    ViewGroup root;
    @BindView(R.id.token_search_text_view)
    TokenSearchTextView searchView;
    @BindView(R.id.token_search_expand)
    View expand;
    @BindView(R.id.token_search_collapse)
    View collapse;

    public SearchBarHolder(@NonNull BaseActivity activity, @NonNull SearchBarModel model) {
        this.model = model;
        ButterKnife.bind(this, activity);
        setupWidth();
    }

    public void expand() {
        expand.setVisibility(View.GONE);
        collapse.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.VISIBLE);
        setupWidth();
        searchView.requestFocusFromTouch();
        InputMethodManager imm = (InputMethodManager) searchView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, 0);
    }

    public void collapse() {
        expand.setVisibility(View.VISIBLE);
        collapse.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);
        setupWidth();
        InputMethodManager imm = (InputMethodManager) searchView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    @NonNull
    public Observable<Void> expandClicked() {
        return RxView.clicks(expand);
    }

    @NonNull
    public Observable<Void> collapseClicked() {
        return RxView.clicks(collapse);
    }

    @NonNull
    public Observable<SearchResult> searchResults() {
        return RxTokenSearchView.create(searchView);
    }

    public void clearText() {
        searchView.clear();
        searchView.getText().clear();
    }

    private void setupWidth() {
        ViewGroup.LayoutParams layoutParams = root.getLayoutParams();
        layoutParams.width = model.isExpanded() ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
        root.setLayoutParams(layoutParams);
        root.invalidate();
    }

    public boolean hasText() {
        return !searchView.getText().toString().isEmpty();
    }
}
