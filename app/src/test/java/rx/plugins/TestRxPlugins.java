package rx.plugins;

import com.github.st1hy.countthemcalories.core.rx.Schedulers;

import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;

public class TestRxPlugins {

    public static void registerImmediateMainThreadHook() {
        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return rx.schedulers.Schedulers.immediate();
            }
        });
    }

    public static void reset() {
        RxAndroidPlugins.getInstance().reset();
        Schedulers.reset();
    }


    public static void registerImmediateHookIO() {
        RxAndroidPlugins.getInstance().reset();
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return rx.schedulers.Schedulers.immediate();
            }
        });
        Schedulers.registerHook(new Schedulers.Hook() {
            @Override
            public Scheduler io() {
                return rx.schedulers.Schedulers.immediate();
            }

            @Override
            public Scheduler computation() {
                return rx.schedulers.Schedulers.computation();
            }

            @Override
            public Scheduler immediate() {
                return rx.schedulers.Schedulers.immediate();
            }
        });
    }
}
