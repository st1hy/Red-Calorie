package com.github.st1hy.countthemcalories.core.callbacks;

import android.support.annotation.NonNull;

public interface OnItemLongClicked<T> {
    void onItemLongClicked(@NonNull T data);
}
