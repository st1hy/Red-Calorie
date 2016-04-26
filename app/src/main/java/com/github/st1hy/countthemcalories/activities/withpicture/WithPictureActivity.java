package com.github.st1hy.countthemcalories.activities.withpicture;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.rx.RxPicassoCallback;
import com.github.st1hy.countthemcalories.core.rx.SimpleObserver;
import com.github.st1hy.countthemcalories.core.ui.BaseActivity;
import com.squareup.picasso.Picasso;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.BuildConfig;
import timber.log.Timber;

public abstract class WithPictureActivity extends BaseActivity implements WithPictureView {
    public static final int REQUEST_CAMERA = 0x3901;
    public static final int REQUEST_PICK_IMAGE = 0x3902;

    protected final RxPicassoCallback picassoLoaderCallback = new RxPicassoCallback();
    protected final CompositeSubscription subscriptions = new CompositeSubscription();

    protected abstract ImageView getImageView();

    protected abstract Picasso getPicasso();

    protected abstract WithPicturePresenter getPresenter();

    @Override
    public void showSelectImageInputDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.add_meal_image_select_title)
                .setItems(R.array.add_meal_image_select_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getPresenter().onSelectedImageSource(ImageSource.fromItemPos(which));
                    }
                })
                .show();
    }


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
                    getPresenter().onImageReceived(imageUri);
                else if (BuildConfig.DEBUG)
                    Timber.w("Received image but intent doesn't have image uri!");
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void setImageToView(@NonNull Uri uri) {
        Subscription subscription = intoPicassoObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NotifyPresenterAboutLoadingResult(getPresenter()));
        subscriptions.add(subscription);
        picassoLoaderCallback.onStarted();
        getPicasso().load(uri)
                .centerCrop()
                .fit()
                .into(getImageView(), picassoLoaderCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPicasso().cancelRequest(getImageView());
        subscriptions.unsubscribe();
    }

    @NonNull
    public Observable<RxPicassoCallback.PicassoLoadingEvent> intoPicassoObservable() {
        return picassoLoaderCallback.intoObservable();
    }

    private static class NotifyPresenterAboutLoadingResult extends SimpleObserver<RxPicassoCallback.PicassoLoadingEvent> {
        private final WithPicturePresenter presenter;

        public NotifyPresenterAboutLoadingResult(@NonNull WithPicturePresenter presenter) {
            this.presenter = presenter;
        }

        @Override
        public void onNext(RxPicassoCallback.PicassoLoadingEvent event) {
            switch (event) {
                case SUCCESS:
                    presenter.onImageLoadingSuccess();
                    break;
                case ERROR:
                    presenter.onImageLoadingFailed();
                    break;
            }
        }
    }
}
