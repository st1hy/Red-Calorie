package com.github.st1hy.countthemcalories.rules;

import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.inject.DaggerApplicationTestComponent;
import com.github.st1hy.countthemcalories.inject.application.ApplicationModule;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ApplicationComponentRule implements TestRule {

    private final Context context;

    public ApplicationComponentRule(@NonNull Context context) {
        this.context = context;
    }

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

        CaloriesCounterApplication application = CaloriesCounterApplication.get(context);
        ApplicationTestComponent build = DaggerApplicationTestComponent.builder()
                .applicationModule(new ApplicationModule(application))
                .build();

        application.setComponent(build);
    }
}
