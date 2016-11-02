package com.github.st1hy.countthemcalories.core.withpicture.imageholder;


import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Uses picture that indicates that new picture can be added as placeholder.
 * Doesn't use placeholder when loading image; so that when switching between one image and another
 * placeholder is not showed in mean time.
 */
public class HeaderImageHolderDelegate extends WithoutPlaceholderImageHolderDelegate {

    @Inject
    public HeaderImageHolderDelegate(@NonNull Picasso picasso,
                                     @NonNull PermissionsHelper permissionsHelper,
                                     @NonNull Provider<ImageView> imageViewProvider) {
        super(picasso, permissionsHelper, imageViewProvider);
        setImagePlaceholder(R.drawable.ic_add_a_photo_24px);
    }
}
