package com.github.st1hy.countthemcalories.core.headerpicture;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.activityresult.ActivityResult;
import com.github.st1hy.countthemcalories.core.activityresult.RxActivityResult;
import com.github.st1hy.countthemcalories.core.rx.Filters;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import org.greenrobot.greendao.annotation.NotNull;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.functions.Func1;

@PerFragment
public class PicturePickerImpl implements PicturePicker {

    private static final int REQUEST_CAMERA = 0x3901;
    private static final int REQUEST_PICK_IMAGE = 0x3902;

    @Nullable
    private Uri tempImageUri;

    @NonNull
    private final Context context;
    @NonNull
    private final RxActivityResult rxActivityResult;

    @Inject
    public PicturePickerImpl(@NonNull @Named("activityContext") Context context,
                             @NonNull RxActivityResult rxActivityResult,
                             @Nullable @Named("pictureTempUri") Uri uri) {
        this.context = context;
        this.rxActivityResult = rxActivityResult;
        this.tempImageUri = uri;
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(SAVE_TEMP_URI, tempImageUri);
    }

    @Override
    public Observable.Transformer<ImageSource, Uri> pickImage() {
        return new Observable.Transformer<ImageSource, Uri>() {
            @Override
            public Observable<Uri> call(Observable<ImageSource> imageSourceObservable) {
                Observable<ImageSource> sharedSource = imageSourceObservable.share();
                return sharedSource.flatMap(startActivityRequest())
                        .mergeWith(rxActivityResult.attachToExistingRequest(REQUEST_CAMERA))
                        .mergeWith(rxActivityResult.attachToExistingRequest(REQUEST_PICK_IMAGE))
                        .distinctUntilChanged()
                        .map(intoUri())
                        .filter(Functions.NOT_NULL)
                        .mergeWith(
                                sharedSource.filter(Filters.equalTo(ImageSource.REMOVE_SOURCE))
                                .map(Functions.into(Uri.EMPTY))
                        );
            }
        };
    }

    @NonNull
    private Func1<ImageSource, Observable<ActivityResult>> startActivityRequest() {
        return new Func1<ImageSource, Observable<ActivityResult>>() {
            @Override
            public Observable<ActivityResult> call(ImageSource imageSource) {
                switch (imageSource) {
                    case CAMERA:
                        return openCameraAndGetPicture();
                    case GALLERY:
                        return pickImageFromGallery();
                    default:
                        return Observable.empty();
                }
            }
        };
    }

    @NotNull
    private Observable<ActivityResult> openCameraAndGetPicture() {

        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        tempImageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        return rxActivityResult.from(context)
                .startActivityForResult(intent, REQUEST_CAMERA);

    }


    @NotNull
    public Observable<ActivityResult> pickImageFromGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.addCategory(Intent.CATEGORY_OPENABLE);
        getIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, context.getString(R.string.add_meal_image_gallery_picker));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        return rxActivityResult.from(context)
                .startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE);
    }

    @NonNull
    private Func1<ActivityResult, Uri> intoUri() {
        return new Func1<ActivityResult, Uri>() {
            @Override
            public Uri call(ActivityResult activityResult) {
                switch (activityResult.getRequestCode()) {
                    case REQUEST_CAMERA:
                        Uri uri = tempImageUri;
                        tempImageUri = null;
                        return uri;
                    case REQUEST_PICK_IMAGE:
                        Intent data = activityResult.getData();
                        return data != null ? data.getData() : null;
                    default:
                        return null;
                }
            }
        };
    }

}
