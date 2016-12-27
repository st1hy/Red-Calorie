package com.github.st1hy.countthemcalories.inject.core.headerpicture;

import com.github.st1hy.countthemcalories.core.headerpicture.PicturePicker;
import com.github.st1hy.countthemcalories.core.headerpicture.PicturePickerImpl;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class PicturePickerModule {

    @Provides
    @PerFragment
    public PicturePicker pictureViewController(PicturePickerImpl picturePicker) {
        return picturePicker;
    }
}
