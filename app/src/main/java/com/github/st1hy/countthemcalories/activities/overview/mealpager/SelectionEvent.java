package com.github.st1hy.countthemcalories.activities.overview.mealpager;

public class SelectionEvent {
    private final int page;
    private final boolean requestScroll;

    private SelectionEvent(int page, boolean requestScroll) {
        this.page = page;
        this.requestScroll = requestScroll;
    }

    public static SelectionEvent of(int page, boolean requestScroll) {
        return new SelectionEvent(page, requestScroll);
    }

    public int getPage() {
        return page;
    }

    public boolean isRequestScroll() {
        return requestScroll;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SelectionEvent that = (SelectionEvent) o;

        return page == that.page;

    }

    @Override
    public int hashCode() {
        return page;
    }
}
