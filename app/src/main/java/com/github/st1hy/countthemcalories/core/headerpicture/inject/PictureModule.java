package com.github.st1hy.countthemcalories.core.headerpicture.inject;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.github.st1hy.countthemcalories.core.headerpicture.PicturePicker;
import com.github.st1hy.countthemcalories.core.headerpicture.PicturePickerImpl;
import com.github.st1hy.countthemcalories.core.headerpicture.PictureView;
import com.github.st1hy.countthemcalories.core.headerpicture.PictureViewImpl;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.github.st1hy.countthemcalories.core.rx.activityresult.RxActivityResult;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class PictureModule {

    @Provides
    public PicturePicker pictureViewController(Context context, RxActivityResult rxActivityResult,
                                               @Named("pictureTempUri") Uri uri) {
        return new PicturePickerImpl(context, rxActivityResult, uri);
    }

    @PerActivity
    @Provides
    public PictureView pictureView(Activity activity) {
        return new PictureViewImpl(activity);
    }

}
