package com.github.st1hy.countthemcalories.rules;

import android.os.StrictMode;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ApplicationComponentRule implements TestRule {

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                setup();
                base.evaluate();
            }
        };
    }

    private void setup() {
        //AndroidJUnit4 seems to be leaking activities by itself
        StrictMode.setVmPolicy(StrictMode.VmPolicy.LAX);
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);
    }
}
