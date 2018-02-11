package com.github.st1hy.countthemcalories.ui.inject.settings;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.activities.settings.view.SettingsFragment;
import com.github.st1hy.countthemcalories.ui.core.drawer.DrawerMenuItem;
import com.github.st1hy.countthemcalories.ui.core.FragmentLocation;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentTransactionModule;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module(includes = FragmentTransactionModule.class)
public abstract class SettingsActivityModule {

    private static final String SETTINGS_CONTENT_TAG = "settings_content_fragment";

    @Binds
    public abstract SettingsFragmentComponentFactory fragmentComponentFactory(SettingsActivityComponent component);

    @Provides
    public static DrawerMenuItem drawerMenuItem() {
        return DrawerMenuItem.SETTINGS;
    }

    @Provides
    @IntoMap
    @StringKey(SETTINGS_CONTENT_TAG)
    @Reusable
    public static FragmentLocation settingsFragment(SettingsFragmentComponentFactory factory) {
        return new FragmentLocation.Builder<>(SettingsFragment.class, SETTINGS_CONTENT_TAG)
                .setViewRootId(R.id.settings_content_root)
                .setInjector(fragment -> fragment.setComponentFactory(factory))
                .build();
    }

}
