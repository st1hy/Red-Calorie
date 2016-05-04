package rx.plugins;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.TestImmediateScheduler;

public class TestRxPlugins {

    public static void registerImmediateHook() {

        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return TestImmediateScheduler.instance();
            }
        });
    }

    public static void reset() {
        RxJavaPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().reset();
    }


    public static void registerImmediateHook2() {
        RxJavaPlugins.getInstance().reset();
        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaSchedulersHook() {
            @Override
            public Scheduler getIOScheduler() {
                return TestImmediateScheduler.instance();
            }

            @Override
            public Scheduler getComputationScheduler() {
                return TestImmediateScheduler.instance();
            }
        });
        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return TestImmediateScheduler.instance();
            }
        });
    }
}
