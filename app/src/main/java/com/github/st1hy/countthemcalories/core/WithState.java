package com.github.st1hy.countthemcalories.core;

import android.os.Bundle;
import android.support.annotation.NonNull;

public interface WithState {

    void onSaveState(@NonNull Bundle outState);
}
