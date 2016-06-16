package com.github.st1hy.countthemcalories.core.tokensearch.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
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

import java.util.List;

public class TokenSearchView extends TokenCompleteTextView<String> {

    public static final String SAVE_TOKEN_COMPLETE = "TokenCompleteTextView";
    public static final String SAVE_CURRENT_COMPLETION_TEXT = "currentCompletionText";
    private TextWatcher textWatcher;
    private SearchChanged searchChanged = null;
    private TokenListener<String> childListener;

    public TokenSearchView(Context context) {
        super(context);
        init();
    }

    public TokenSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TokenSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        allowCollapse(true);
        performBestGuess(false);
        setTokenClickStyle(TokenClickStyle.Delete);
        setDeletionStyle(TokenDeleteStyle.PartialCompletion);
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

    public void setSearchChanged(@Nullable SearchChanged searchChanged) {
        this.searchChanged = searchChanged;
    }


    @Override
    protected void addListeners() {
        super.addListeners();
        removeTextChangedListener(getTextWatcher());
        addTextChangedListener(getTextWatcher());
    }

    @Override
    protected void removeListeners() {
        super.removeListeners();
        removeTextChangedListener(getTextWatcher());
    }

    @Override
    protected View getViewForObject(String query) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = (ViewGroup) TokenSearchView.this.getParent();
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
        if (searchChanged != null) {
            searchChanged.onSearching(text, objects);
        }
    }

}
