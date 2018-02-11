package com.github.st1hy.countthemcalories.ui.inject.core;

import com.github.st1hy.countthemcalories.ui.core.headerpicture.PicturePicker;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.PicturePickerImpl;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class PicturePickerModule {

    @Binds
    @PerFragment
    public abstract PicturePicker pictureViewController(PicturePickerImpl picturePicker);
}
