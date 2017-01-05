package com.github.st1hy.countthemcalories.inject.core.headerpicture;

import com.github.st1hy.countthemcalories.core.headerpicture.PicturePicker;
import com.github.st1hy.countthemcalories.core.headerpicture.PicturePickerImpl;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class PicturePickerModule {

    @Binds
    @PerFragment
    public abstract PicturePicker pictureViewController(PicturePickerImpl picturePicker);
}
