package com.github.st1hy.countthemcalories.ui.core.headerpicture.imageholder;


import com.github.st1hy.countthemcalories.R;

import javax.inject.Inject;

/**
 * Uses picture that indicates that new picture can be added as placeholder.
 * Doesn't use placeholder when loading image; so that when switching between one image and another
 * placeholder is not showed in mean time.
 */
public class HeaderImageHolderDelegate extends WithoutPlaceholderImageHolderDelegate {

    @Inject
    public HeaderImageHolderDelegate() {
        super();
        setImagePlaceholder(R.drawable.ic_add_a_photo_24px);
    }
}
