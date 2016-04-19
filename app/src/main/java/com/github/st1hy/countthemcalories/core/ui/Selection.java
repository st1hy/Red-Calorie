package com.github.st1hy.countthemcalories.core.ui;

public enum Selection {
    SELECTED, UNSELECTED;

    /**
     * @return true if selected
     */
    public boolean is() {
        return this == SELECTED;
    }
}
