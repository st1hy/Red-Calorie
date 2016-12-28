package com.github.st1hy.countthemcalories.testutils;

public class TestError extends Error {

    public TestError(Throwable throwable) {
        super(throwable);
    }

    public TestError() {
    }

    public TestError(String detailMessage) {
        super(detailMessage);
    }

    public TestError(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
