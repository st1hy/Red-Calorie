package com.github.st1hy.countthemcalories.inject.core;

import com.github.st1hy.countthemcalories.core.headerpicture.PicturePicker;
import com.github.st1hy.countthemcalories.core.headerpicture.TestPicturePicker;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class TestPicturePickerModule {

    @Provides
    @PerFragment
    public PicturePicker pictureViewController(TestPicturePicker testPicturePicker) {
        return testPicturePicker;
    }
}
