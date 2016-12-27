package com.github.st1hy.countthemcalories.inject.core.headerpicture;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.core.headerpicture.PicturePicker;
import com.github.st1hy.countthemcalories.core.headerpicture.PictureView;
import com.github.st1hy.countthemcalories.core.headerpicture.PictureViewImpl;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.HeaderImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class PictureOthersModule {

    @PerFragment
    @Provides
    public PictureView pictureView(PictureViewImpl pictureView) {
        return pictureView;
    }

    @Provides
    @Nullable
    @Named("pictureTempUri")
    public Uri tempPictureUri(@Nullable @Named("savedState") Bundle savedState) {
        if (savedState != null) {
            return savedState.getParcelable(PicturePicker.SAVE_TEMP_URI);
        } else {
            return null;
        }
    }

    @Provides
    public ImageView imageViewProvider(PictureView view) {
        return view.getImageView();
    }

    @Provides
    @Named("header")
    public ImageHolderDelegate provideImageHolderDelegate(
            HeaderImageHolderDelegate imageHolderDelegate) {
        return imageHolderDelegate;
    }
}
