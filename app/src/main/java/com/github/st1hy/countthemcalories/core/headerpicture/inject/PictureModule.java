package com.github.st1hy.countthemcalories.core.headerpicture.inject;

import com.github.st1hy.countthemcalories.core.headerpicture.PicturePicker;
import com.github.st1hy.countthemcalories.core.headerpicture.PicturePickerImpl;
import com.github.st1hy.countthemcalories.core.headerpicture.PictureView;
import com.github.st1hy.countthemcalories.core.headerpicture.PictureViewImpl;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class PictureModule {

    @Provides
    @PerFragment
    public PicturePicker pictureViewController(PicturePickerImpl picturePicker) {
        return picturePicker;
    }

    @PerFragment
    @Provides
    public PictureView pictureView(PictureViewImpl pictureView) {
        return pictureView;
    }

}
