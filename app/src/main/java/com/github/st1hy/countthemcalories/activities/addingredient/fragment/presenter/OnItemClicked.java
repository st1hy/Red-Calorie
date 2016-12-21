package com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter;

import android.support.annotation.NonNull;

public interface OnItemClicked<T> {
    void onItemClicked(@NonNull T data);
}
