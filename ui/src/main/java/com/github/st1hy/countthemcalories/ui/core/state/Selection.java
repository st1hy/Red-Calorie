package com.github.st1hy.countthemcalories.ui.core.state;

public enum Selection {
    SELECTED, UNSELECTED;

    /**
     * @return true if selected
     */
    public boolean isSelected() {
        return this == SELECTED;
    }
}
