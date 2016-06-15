package com.github.st1hy.countthemcalories.activities.ingredients.view.searchview;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.tokenautocomplete.TokenCompleteTextView;

public class IngredientsSearchView extends TokenCompleteTextView<Query> {

    public IngredientsSearchView(Context context) {
        super(context);
        init();
    }

    public IngredientsSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IngredientsSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        allowCollapse(true);
        performBestGuess(false);
        setTokenClickStyle(TokenClickStyle.Delete);
    }

    @Override
    protected View getViewForObject(Query query) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = (ViewGroup) IngredientsSearchView.this.getParent();
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.ingredients_chip, parent, false);
        TextView name = (TextView) view.findViewById(R.id.chip_name);
        name.setText(query.getQuery());
        return view;
    }

    @Override
    protected Query defaultObject(String completionText) {
        return new Query(completionText);
    }
}
