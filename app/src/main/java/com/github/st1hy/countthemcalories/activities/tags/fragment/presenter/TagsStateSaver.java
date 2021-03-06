package com.github.st1hy.countthemcalories.activities.tags.fragment.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.tags.fragment.model.TagsFragmentModel;
import com.github.st1hy.countthemcalories.core.WithState;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import org.parceler.Parcels;

import javax.inject.Inject;

@PerFragment
public class TagsStateSaver implements WithState {

    public static final String MODEL = "fragment state";

    @NonNull
    private final TagsFragmentModel model;

    @Inject
    public TagsStateSaver(@NonNull TagsFragmentModel model) {
        this.model = model;
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(MODEL, Parcels.wrap(model));
    }
}
