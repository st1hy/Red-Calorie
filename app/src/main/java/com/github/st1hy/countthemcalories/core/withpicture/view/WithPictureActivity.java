package com.github.st1hy.countthemcalories.core.withpicture.view;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import timber.log.BuildConfig;
import timber.log.Timber;

public abstract class WithPictureActivity extends BaseActivity implements WithPictureView {

    public static final int REQUEST_CAMERA = 0x3901;
    public static final int REQUEST_PICK_IMAGE = 0x3902;

    final BehaviorSubject<Uri> pictureSelectedSubject = BehaviorSubject.create();

    @NonNull
    public abstract ImageView getImageView();

    @Override
    public void openCameraAndGetPicture() {
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CAMERA);
    }

    @Override
    public void pickImageFromGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.addCategory(Intent.CATEGORY_OPENABLE);
        getIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.add_meal_image_gallery_picker));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_IMAGE || requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                if (imageUri != null)
                    pictureSelectedSubject.onNext(imageUri);
                else if (BuildConfig.DEBUG)
                    Timber.w("Received image but intent doesn't have image uri!");
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    @NonNull
    @Override
    public Observable<Uri> getPictureSelectedObservable() {
        return pictureSelectedSubject.asObservable();
    }

}
