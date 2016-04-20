package com.github.st1hy.countthemcalories.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Retries failed tests n-times if they failed.
 */
public class RetryRule implements TestRule {
    private final int tryCount;

    public RetryRule(int tryCount) {
        this.tryCount = tryCount;
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                int i = 0;
                while (true) {
                    try {
                        base.evaluate();
                        break;
                    } catch (Throwable throwable) {
                        if (i == tryCount) throw throwable;
                        i++;
                    }
                }
            }
        };
    }
}
