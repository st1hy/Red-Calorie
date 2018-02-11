package com.github.st1hy.countthemcalories.ui.core.headerpicture;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityLauncher;
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityResult;
import com.github.st1hy.countthemcalories.ui.core.activityresult.StartParams;
import com.github.st1hy.countthemcalories.ui.core.rx.Filters;
import com.github.st1hy.countthemcalories.ui.core.rx.Functions;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.context.ActivityContext;

import org.greenrobot.greendao.annotation.NotNull;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

@PerFragment
public class PicturePickerImpl implements PicturePicker {

    private static final int REQUEST_CAMERA = 0x3901;
    private static final int REQUEST_PICK_IMAGE = 0x3902;

    @Nullable
    private Uri tempImageUri;

    @NonNull
    private final Context context;
    @NonNull
    private final ActivityLauncher activityLauncher;

    @Inject
    public PicturePickerImpl(@NonNull @ActivityContext Context context,
                             @NonNull ActivityLauncher activityLauncher,
                             @Nullable @Named("pictureTempUri") Uri uri) {
        this.context = context;
        this.activityLauncher = activityLauncher;
        this.tempImageUri = uri;
    }

    @Override
    public void onSaveState(@NonNull Bundle outState) {
        outState.putParcelable(SAVE_TEMP_URI, tempImageUri);
    }

    @NonNull
    @CheckResult
    @Override
    public Observable.Transformer<ImageSource, Uri> pickImage() {
        return imageSourceObservable -> {
            Observable<ImageSource> sharedSource = imageSourceObservable.share();
            return sharedSource.filter(Filters.equalTo(ImageSource.CAMERA))
                    .map(source -> imageFromCameraParams())
                    .compose(activityLauncher.startActivityForResult(REQUEST_CAMERA))
                    .mergeWith(
                            sharedSource
                                    .filter(Filters.equalTo(ImageSource.GALLERY))
                                    .map(source -> imageFromGalleryParams())
                                    .compose(activityLauncher
                                            .startActivityForResult(REQUEST_PICK_IMAGE)
                                    )
                    )
                    .filter(ActivityResult.IS_OK)
                    .map(this::extractUriFrom)
                    .filter(Functions.NOT_NULL)
                    .mergeWith(
                            sharedSource.filter(Filters.equalTo(ImageSource.REMOVE_SOURCE))
                                    .map(Functions.into(Uri.EMPTY))
                    );
        };
    }

    @NotNull
    private StartParams imageFromCameraParams() {
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        tempImageUri = context.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        return StartParams.of(intent, REQUEST_CAMERA);

    }

    @NotNull
    private StartParams imageFromGalleryParams() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.addCategory(Intent.CATEGORY_OPENABLE);
        getIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, context.getString(R.string.add_meal_image_gallery_picker));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        return StartParams.of(chooserIntent, REQUEST_PICK_IMAGE);
    }


    @Nullable
    private Uri extractUriFrom(ActivityResult activityResult) {
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

    @VisibleForTesting
    void setTempImageUri(@Nullable Uri tempImageUri) {
        this.tempImageUri = tempImageUri;
    }
}
