package com.github.st1hy.countthemcalories.activities.tags.fragment.adapter.holder;

import android.support.annotation.NonNull;

public interface OnTagInteraction {

    void onTagClicked(int position, @NonNull TagItemHolder holder);

    void onDeleteClicked(int position, @NonNull TagItemHolder holder);

    void onEditClicked(int position, @NonNull TagItemHolder holder);
}
