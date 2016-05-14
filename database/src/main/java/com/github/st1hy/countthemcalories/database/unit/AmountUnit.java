package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;

public interface AmountUnit extends Unit {

    @NonNull
    AmountUnitType getType();
}
