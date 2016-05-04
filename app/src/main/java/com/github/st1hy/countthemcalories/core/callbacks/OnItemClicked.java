package com.github.st1hy.countthemcalories.core.callbacks;

import android.support.annotation.NonNull;

public interface OnItemClicked<T> {
    void onItemClicked(@NonNull T data);
}
