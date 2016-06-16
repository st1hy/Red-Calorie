package com.github.st1hy.countthemcalories.core.tokensearch.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.tokensearch.model.RxTokenSearchView;
import com.github.st1hy.countthemcalories.core.tokensearch.model.SearchResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

public class TokenSearchView extends FrameLayout {

    @BindView(R.id.token_search_root)
    ViewGroup root;
    @BindView(R.id.token_search_text_view)
    TokenSearchTextView searchView;
    @BindView(R.id.token_search_expand)
    View expand;
    @BindView(R.id.token_search_collapse)
    View collapse;

    boolean isExpanded = false;

    public TokenSearchView(Context context) {
        super(context);
        init();
    }

    public TokenSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TokenSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public TokenSearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        addView(inflateView(LayoutInflater.from(getContext())));
        ButterKnife.bind(this);
        setupState();
    }

    protected View inflateView(@NonNull LayoutInflater inflater) {
        return inflater.inflate(R.layout.token_search_view, this, false);
    }


    public void expand() {
        isExpanded = true;
        expand.setVisibility(View.GONE);
        collapse.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.VISIBLE);
        setupWidth();
        searchView.requestFocusFromTouch();
        InputMethodManager imm = (InputMethodManager) searchView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, 0);
    }

    @OnClick(R.id.token_search_collapse)
    public void collapse() {
        isExpanded = false;
        expand.setVisibility(View.VISIBLE);
        collapse.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);
        setupWidth();
        InputMethodManager imm = (InputMethodManager) searchView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    @OnClick(R.id.token_search_expand)
    public void onExpandClicked() {
        if (hasText()) {
            clearText();
        } else {
            collapse();
        }
    }

    public void setQuery(String query, boolean submit) {
        clearText();
        searchView.append(query);
    }

    public void clearText() {
        searchView.clear();
        searchView.getText().clear();
    }

    @NonNull
    public Observable<SearchResult> searchResults() {
        return RxTokenSearchView.create(searchView);
    }

    private void setupWidth() {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams == null) {
            getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    getViewTreeObserver().removeOnPreDrawListener(this);
                    setupWidth();
                    return true;
                }
            });
        } else {
            layoutParams.width = isExpanded ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
            setLayoutParams(layoutParams);
            layoutParams = root.getLayoutParams();
            layoutParams.width = isExpanded ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT;
            root.setLayoutParams(layoutParams);
            invalidate();
        }
    }

    public boolean hasText() {
        return !searchView.getText().toString().isEmpty();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return new SavedState(super.onSaveInstanceState(), this);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if (!(state instanceof SavedState)) {
            return;
        }
        SavedState savedState = (SavedState) state;
        isExpanded = savedState.isExpanded;
        setupState();
    }

    protected void setupState() {
        if (isExpanded) expand();
        else collapse();
    }

    private static class SavedState extends BaseSavedState {
        private boolean isExpanded;

        public SavedState(Parcel source) {
            super(source);
            isExpanded = source.readInt() > 0;
        }

        public SavedState(Parcelable superState, TokenSearchView view) {
            super(superState);
            isExpanded = view.isExpanded;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(isExpanded ? 1 : 0);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
