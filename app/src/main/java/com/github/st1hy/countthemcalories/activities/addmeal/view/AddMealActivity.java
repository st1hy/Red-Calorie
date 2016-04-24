package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityModule;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.DaggerAddMealActivityComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.AddMealPresenter;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.ImageSource;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.core.rx.RxPicassoCallback;
import com.github.st1hy.countthemcalories.core.rx.SimpleObserver;
import com.github.st1hy.countthemcalories.core.ui.BaseActivity;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.BuildConfig;
import timber.log.Timber;

public class AddMealActivity extends BaseActivity implements AddMealView {
    public static final int REQUEST_CAMERA = 0x3901;
    public static final int REQUEST_PICK_IMAGE = 0x3902;

    @Inject
    AddMealPresenter presenter;
    @Inject
    Picasso picasso;

    @Bind(R.id.add_meal_toolbar)
    Toolbar toolbar;
    @Bind(R.id.add_meal_save_button)
    Button saveButton;
    @Bind(R.id.add_meal_image)
    ImageView mealImage;
    @Bind(R.id.add_meal_ingredients_list)
    RecyclerView ingredientList;
    @Bind(R.id.add_meal_empty_ingredients)
    View emptyIngredients;
    @Bind(R.id.add_meal_button_add_ingredient)
    Button addIngredientButton;
    @Bind(R.id.add_meal_fab_add_ingredient)
    FloatingActionButton addIngredientFab;

    AddMealActivityComponent component;
    final RxPicassoCallback picassoLoaderCallback = new RxPicassoCallback();
    final CompositeSubscription subscriptions = new CompositeSubscription();

    @NonNull
    protected AddMealActivityComponent getComponent() {
        if (component == null) {
            component = DaggerAddMealActivityComponent.builder()
                    .applicationComponent(getAppComponent())
                    .addMealActivityModule(new AddMealActivityModule(this))
                    .build();
        }
        return component;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_meal_activity);
        ButterKnife.bind(this);
        getComponent().inject(this);
        setSupportActionBar(toolbar);
        assertNotNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSaveButtonClicked();
            }
        });
        mealImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onImageClicked();
            }
        });
        ingredientList.setAdapter(presenter.getIngredientListAdapter());
        ingredientList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void openOverviewActivity() {
        Intent intent = new Intent(this, OverviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void showSelectImageInputDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.add_meal_image_select_title)
                .setItems(R.array.add_meal_image_select_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onSelectedImageSource(ImageSource.fromItemPos(which));
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

        Intent pickIntent = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
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
                    presenter.onImageReceived(imageUri);
                else if (BuildConfig.DEBUG)
                    Timber.w("Received image but intent doesn't have image uri!");
            }
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setImageToView(@NonNull Uri uri) {
        Subscription subscription = intoPicassoObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NotifyPresenterAboutLoadingResult(presenter));
        subscriptions.add(subscription);
        picassoLoaderCallback.onStarted();
        picasso.load(uri)
                .centerCrop()
                .fit()
                .into(mealImage, picassoLoaderCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        picasso.cancelRequest(mealImage);
        subscriptions.unsubscribe();
    }

    @NonNull
    public Observable<RxPicassoCallback.PicassoLoadingEvent> intoPicassoObservable() {
        return picassoLoaderCallback.intoObservable();
    }

    private static class NotifyPresenterAboutLoadingResult extends SimpleObserver<RxPicassoCallback.PicassoLoadingEvent> {
        private final AddMealPresenter presenter;

        public NotifyPresenterAboutLoadingResult(@NonNull AddMealPresenter presenter) {
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
