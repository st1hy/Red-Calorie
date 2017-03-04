package com.github.st1hy.countthemcalories.core.tokensearch;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.Collection;
import java.util.List;

public class TokenSearchTextView extends TokenCompleteTextView<String> implements Searchable {

    public static final String SAVE_TOKEN_COMPLETE = "TokenCompleteTextView";
    public static final String SAVE_CURRENT_COMPLETION_TEXT = "currentCompletionText";
    private TextWatcher textWatcher;
    private OnSearchChanged onSearchChanged = null;
    private TokenCompleteTextView.TokenListener<String> childListener;
    private DropDownDialogChangeListener dropDownDialogChangeListener;

    public TokenSearchTextView(Context context) {
        super(context);
        init();
    }

    public TokenSearchTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TokenSearchTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        allowCollapse(true);
        performBestGuess(false);
        setTokenClickStyle(TokenClickStyle.Delete);
        setDeletionStyle(TokenDeleteStyle.PartialCompletion);
        setThreshold(1);
        setTokenLimit(10);
        super.setTokenListener(new TokenListener<String>() {
            @Override
            public void onTokenAdded(String token) {
                if (childListener != null) childListener.onTokenAdded(token);
                notifyChanged();
            }

            @Override
            public void onTokenRemoved(String token) {
                if (childListener != null) childListener.onTokenRemoved(token);
                notifyChanged();
            }

        });
    }

    @Override
    public void setOnSearchChanged(@Nullable OnSearchChanged onSearchChanged) {
        this.onSearchChanged = onSearchChanged;
    }

    @NonNull
    @Override
    public String getQuery() {
        return currentCompletionText();
    }

    @NonNull
    @Override
    public List<String> getTokens() {
        return getObjects();
    }

    @Override
    protected void addListeners() {
        super.addListeners();
        removeTextChangedListener(getTextWatcher());
        addTextChangedListener(getTextWatcher());
    }

    public void addAll(Collection<String> tokens) {
        for (String token : tokens) {
            addObject(token);
        }
    }

    @Override
    public void showDropDown() {
        super.showDropDown();
        if (dropDownDialogChangeListener != null) dropDownDialogChangeListener.onDialogShown();
    }

    @Override
    public void dismissDropDown() {
        super.dismissDropDown();
        if (dropDownDialogChangeListener != null) dropDownDialogChangeListener.onDialogDismissed();
    }

    @Override
    protected void removeListeners() {
        super.removeListeners();
        removeTextChangedListener(getTextWatcher());
    }

    @Override
    protected View getViewForObject(String query) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = (ViewGroup) TokenSearchTextView.this.getParent();
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.token_chip, parent, false);
        TextView name = (TextView) view.findViewById(R.id.chip_name);
        name.setText(query);
        return view;
    }

    @Override
    protected String defaultObject(String completionText) {
        return completionText;
    }


    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SAVE_TOKEN_COMPLETE, super.onSaveInstanceState());
        bundle.putString(SAVE_CURRENT_COMPLETION_TEXT, currentCompletionText());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof Bundle)) {
            super.onRestoreInstanceState(state);
            return;
        }
        Bundle bundle = (Bundle) state;
        super.onRestoreInstanceState(bundle.getParcelable(SAVE_TOKEN_COMPLETE));
        append(bundle.getString(SAVE_CURRENT_COMPLETION_TEXT));
    }

    private TextWatcher getTextWatcher() {
        if (textWatcher == null) {
            textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    notifyChanged();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };
        }
        return textWatcher;
    }

    @Override
    public void setTokenListener(TokenListener<String> l) {
        childListener = l;
    }

    private void notifyChanged() {
        String text = currentCompletionText();
        List<String> objects = getObjects();
        if (onSearchChanged != null) {
            onSearchChanged.onSearching(text, objects);
        }
    }

    public void setOnDropDownDialogChangeListener(@Nullable DropDownDialogChangeListener listener) {
        this.dropDownDialogChangeListener = listener;
    }

    public interface DropDownDialogChangeListener {
        void onDialogShown();

        void onDialogDismissed();
    }
}
