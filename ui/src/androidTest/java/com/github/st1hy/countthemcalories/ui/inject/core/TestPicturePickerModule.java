package com.github.st1hy.countthemcalories.ui.inject.core;

import com.github.st1hy.countthemcalories.ui.core.headerpicture.PicturePicker;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.TestPicturePicker;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;

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
