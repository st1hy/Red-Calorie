package com.github.st1hy.countthemcalories.core.picture.inject;

import android.content.Context;
import android.net.Uri;

import com.github.st1hy.countthemcalories.core.picture.PictureViewController;
import com.github.st1hy.countthemcalories.core.picture.PictureViewControllerImpl;
import com.github.st1hy.countthemcalories.core.rx.activityresult.RxActivityResult;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class PictureModule {

    @Provides
    public PictureViewController pictureViewController(Context context, RxActivityResult rxActivityResult,
                                                       @Named("pictureTempUri") Uri uri) {
        return new PictureViewControllerImpl(context, rxActivityResult, uri);
    }


}
