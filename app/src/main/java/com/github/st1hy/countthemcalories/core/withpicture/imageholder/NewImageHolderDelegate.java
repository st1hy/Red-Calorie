package com.github.st1hy.countthemcalories.core.withpicture.imageholder;


import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.squareup.picasso.Picasso;

import javax.inject.Provider;

import rx.Observable;

/**
 * Specialized image holder class:
 *
 * Uses add picture as placeholder.
 * Don't use placeholder when loading image.
 */
public class NewImageHolderDelegate extends ImageHolderDelegate {

    public NewImageHolderDelegate(@NonNull Picasso picasso,
                                  @NonNull PermissionsHelper permissionsHelper,
                                  @NonNull Provider<ImageView> imageViewProvider) {
        super(picasso, permissionsHelper, imageViewProvider);
        placeholderResId = R.drawable.ic_add_a_photo_24px;
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
