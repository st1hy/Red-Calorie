package com.github.st1hy.countthemcalories.core.picture;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.activityresult.ActivityResult;
import com.github.st1hy.countthemcalories.core.rx.activityresult.RxActivityResult;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.functions.Func1;

public class PictureViewControllerImpl implements PictureViewController {

    public static final String SAVE_TEMP_URI = "with picture temp uri";
    private static final int REQUEST_CAMERA = 0x3901;
    private static final int REQUEST_PICK_IMAGE = 0x3902;

    private Uri tempImageUri;

    private final Context context;
    private final RxActivityResult rxActivityResult;

    @Inject
    public PictureViewControllerImpl(Context context, RxActivityResult rxActivityResult,
                                     @Named("pictureTempUri") Uri uri) {
        this.context = context;
        this.rxActivityResult = rxActivityResult;
        this.tempImageUri = uri;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(SAVE_TEMP_URI, tempImageUri);
    }

    @Override
    public Observable<Uri> openCameraAndGetPicture() {

        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        tempImageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        return rxActivityResult.from(context).startActivityForResult(intent, REQUEST_CAMERA)
                .filter(ActivityResult.IS_OK)
                .map(new Func1<ActivityResult, Uri>() {
                    @Override
                    public Uri call(ActivityResult activityResult) {
                        Uri uri = tempImageUri;
                        tempImageUri = null;
                        return uri;
                    }
                }).filter(Functions.NOT_NULL);
    }

    @Override
    public Observable<Uri> pickImageFromGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.addCategory(Intent.CATEGORY_OPENABLE);
        getIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, context.getString(R.string.add_meal_image_gallery_picker));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        return rxActivityResult.from(context).startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE)
                .filter(ActivityResult.IS_OK)
                .map(new Func1<ActivityResult, Uri>() {
                    @Override
                    public Uri call(ActivityResult activityResult) {
                        Intent data = activityResult.getData();
                        return data != null ? data.getData() : null;
                    }
                })
                .filter(Functions.NOT_NULL);
    }
}