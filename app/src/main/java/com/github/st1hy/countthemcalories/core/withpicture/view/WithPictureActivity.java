package com.github.st1hy.countthemcalories.core.withpicture.view;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public abstract class WithPictureActivity extends BaseActivity implements WithPictureView {

    private static final String SAVE_TEMP_URI = "with picture temp uri";
    public static final int REQUEST_CAMERA = 0x3901;
    public static final int REQUEST_PICK_IMAGE = 0x3902;

    final BehaviorSubject<Uri> pictureSelectedSubject = BehaviorSubject.create();
    Uri tempImageUri;
    final Observable<Uri> getPictureSelectedObservable = pictureSelectedSubject.asObservable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            tempImageUri = savedInstanceState.getParcelable(SAVE_TEMP_URI);
        }
    }

    @Override
    public void openCameraAndGetPicture() {
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        tempImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, REQUEST_CAMERA);
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
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Timber.d("Request picture result intent: %s, bundle: %s", data,
                    data != null ? data.getExtras() : "null");
            if (requestCode == REQUEST_PICK_IMAGE) {
                if (data != null) {
                    Uri imageUri = data.getData();
                    if (imageUri != null) {
                        pictureSelectedSubject.onNext(imageUri);
                        return;
                    }
                }
                Timber.w("Received image but intent doesn't have image uri! %s", data);
            }
            if (requestCode == REQUEST_CAMERA) {
                if (tempImageUri != null) {
                    pictureSelectedSubject.onNext(tempImageUri);
                    tempImageUri = null;
                }
            }
        }
    }

    @NonNull
    @Override
    public Observable<Uri> getPictureSelectedObservable() {
        return getPictureSelectedObservable;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVE_TEMP_URI, tempImageUri);
    }
}
