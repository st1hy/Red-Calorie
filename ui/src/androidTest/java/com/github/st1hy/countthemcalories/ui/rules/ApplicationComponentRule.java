package com.github.st1hy.countthemcalories.ui.rules;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.test.runner.permission.PermissionRequester;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.ui.contract.AppComponentProvider;
import com.github.st1hy.countthemcalories.ui.contract.AppComponentRepository;
import com.github.st1hy.countthemcalories.ui.inject.app.ApplicationModule;
import com.github.st1hy.countthemcalories.ui.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.inject.DaggerApplicationTestComponent;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ApplicationComponentRule implements TestRule {

    private final AppComponentProvider application;

    public ApplicationComponentRule(@NonNull Context context) {
        this.application = (AppComponentProvider) context.getApplicationContext();
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

        AppComponentRepository.component = DaggerApplicationTestComponent.builder()
                .applicationModule(new ApplicationModule(application))
                .build();

        PermissionRequester permissionRequester = new PermissionRequester();
        permissionRequester.addPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionRequester.requestPermissions();
    }
}
