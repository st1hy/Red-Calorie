package com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.database.Tag;

public interface OnTagInteraction {

    void onItemClicked(int position, @NonNull Tag tag);

    void onItemLongClicked(int position, @NonNull Tag tag);
}
