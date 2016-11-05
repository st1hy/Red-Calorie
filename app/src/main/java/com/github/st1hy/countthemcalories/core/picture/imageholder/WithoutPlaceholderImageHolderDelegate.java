package com.github.st1hy.countthemcalories.core.picture.imageholder;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;

/**
 * Doesn't use placeholder when loading image; so that when switching between one image and another
 * placeholder is not showed in mean time.
 */
public class WithoutPlaceholderImageHolderDelegate extends ImageHolderDelegate {

    @Inject
    public WithoutPlaceholderImageHolderDelegate(Picasso picasso,
                                                 PermissionsHelper permissionsHelper,
                                                 Provider<ImageView> imageViewProvider) {
        super(picasso, permissionsHelper, imageViewProvider);
    }

    @NonNull
    @Override
    protected Observable<RxPicasso.PicassoEvent> loadImage(@NonNull Uri uri) {
        return RxPicasso.Builder.with(picasso, uri)
                .centerCrop()
                .fit()
                .into(imageViewProvider.get())
                .asObservable();
    }
}
