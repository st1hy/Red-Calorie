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

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class PictureOthersModule {

    @PerFragment
    @Binds
    public abstract PictureView pictureView(PictureViewImpl pictureView);

    @Provides
    @Nullable
    @Named("pictureTempUri")
    public static Uri tempPictureUri(@Nullable @Named("savedState") Bundle savedState) {
        if (savedState != null) {
            return savedState.getParcelable(PicturePicker.SAVE_TEMP_URI);
        } else {
            return null;
        }
    }

    @Provides
    @PerFragment
    @Named("imageLoaderImageView")
    public static ImageView imageViewProvider(PictureView view) {
        return view.getImageView();
    }

    @Binds
    @Named("header")
    public abstract ImageHolderDelegate provideImageHolderDelegate(HeaderImageHolderDelegate delegate);
}
