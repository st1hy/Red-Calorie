package com.github.st1hy.countthemcalories.ui.core.headerpicture.imageholder;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.ui.R;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Workaround for activity transition view issues: cropping and size of transitioning image.
 */
public class DetailImageHolderDelegate extends WithoutPlaceholderImageHolderDelegate {

    @Inject
    Resources resources;

    @Inject
    public DetailImageHolderDelegate() {
    }

    @Override
    public void displayImage(@NonNull Uri uri) {
        setImageViewSquare(Uri.EMPTY.equals(uri));
        super.displayImage(uri);
    }

    @Override
    protected void onLoadingError() {
        super.onLoadingError();
        // Correct the size of image view so placeholder is not cropped
        setImageViewSquare(true);
    }

    private void setImageViewSquare(boolean isSquare) {
        ImageView imageView = imageViewProvider.get();
        if (!(imageView instanceof CircleImageView)) {
            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            int size = resources.getDimensionPixelSize(R.dimen.image_detail_large_height);
            layoutParams.height = size;
            layoutParams.width = isSquare ? size : RecyclerView.LayoutParams.MATCH_PARENT;
            imageView.setLayoutParams(layoutParams);
        }
    }

}
