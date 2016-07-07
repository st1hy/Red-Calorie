package com.github.st1hy.countthemcalories.core.withpicture.view;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.withpicture.presenter.WithPicturePresenter;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.BuildConfig;
import timber.log.Timber;

public abstract class WithPictureActivity extends BaseActivity implements WithPictureView {
    public static final int REQUEST_CAMERA = 0x3901;
    public static final int REQUEST_PICK_IMAGE = 0x3902;

    @Inject
    WithPicturePresenter presenter;
    @Inject
    Picasso picasso;

    protected abstract ImageView getImageView();

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
        Timber.d("On activity result!");
        if (requestCode == REQUEST_PICK_IMAGE || requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                if (imageUri != null)
                    presenter.onImageReceived(imageUri);
                else if (BuildConfig.DEBUG)
                    Timber.w("Received image but intent doesn't have image uri!");
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Observable<RxPicasso.PicassoEvent> showImage(@NonNull Uri uri) {
        return RxPicasso.Builder.with(picasso, uri)
                .centerCrop()
                .fit()
                .into(getImageView())
                .asObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<RxPicasso.PicassoEvent>() {
                    @Override
                    public void call(RxPicasso.PicassoEvent event) {
                        onImageShown();
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    protected void onImageShown() {

    }
}
