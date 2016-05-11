package com.github.st1hy.countthemcalories.database.unit;

import android.support.annotation.NonNull;

import java.math.BigDecimal;

public enum EnergyUnit {
    KJ(BigDecimal.ONE),
    KCAL(BigDecimal.valueOf(4184, 3))
    ;
    private final BigDecimal inKJ;

    EnergyUnit(BigDecimal inKJ) {
        this.inKJ = inKJ;
    }

    @NonNull
    public BigDecimal getInKJ() {
        return inKJ;
    }
}
