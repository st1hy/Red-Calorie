package com.github.st1hy.countthemcalories.activities.contribute.fragment.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.google.common.base.Preconditions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContributeFragment extends BaseFragment {

    @BindView(R.id.contribute_link)
    TextView link;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contribute_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, Preconditions.checkNotNull(getView()));
        link.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
