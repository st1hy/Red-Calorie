package com.github.st1hy.countthemcalories.ui.inject.core;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.ui.core.headerpicture.PicturePicker;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.PictureView;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.PictureViewImpl;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.imageholder.HeaderImageHolderDelegate;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.bundle.FragmentSavedState;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.HeaderImageView;

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
    static Uri tempPictureUri(@Nullable @FragmentSavedState Bundle savedState) {
        if (savedState != null) {
            return savedState.getParcelable(PicturePicker.SAVE_TEMP_URI);
        } else {
            return null;
        }
    }

    @Provides
    @PerFragment
    @HeaderImageView
    public static ImageView imageViewProvider(PictureView view) {
        return view.getImageView();
    }

    @Binds
    @Named("header")
    public abstract ImageHolderDelegate provideImageHolderDelegate(HeaderImageHolderDelegate delegate);
}
